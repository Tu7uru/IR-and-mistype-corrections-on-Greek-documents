param(
    [Parameter(Mandatory = $true,ParameterSetName = 'Load')]
    [switch]$loadBulk,
    
    [Parameter(Mandatory = $true,ParameterSetName = 'CreateIndex')]
    [Parameter(Mandatory = $true,ParameterSetName = 'CreateIndexManually')]
    [switch]$createIndex,
    
    [Parameter(Mandatory = $false,ParameterSetName = 'CreateIndex')]
    [switch]$RemoveStopwords,
    
    [Parameter(Mandatory = $false,ParameterSetName = 'CreateIndex')]
    [switch]$ApplyStemming,
    
    [Parameter(Mandatory = $false,ParameterSetName = 'CreateIndex')]
    [String]$AnalyzerName,

    [Parameter(Mandatory = $false,ParameterSetName = 'CreateIndexManually')]
    [String]$IndexBodyDefinition,
    
    [Parameter(Mandatory = $true,ParameterSetName = 'Get')]
    [switch]$retrieveDataset,
    
    [Parameter(Mandatory = $true,ParameterSetName = 'Convert')]
    [switch]$convertJsonDocsToBulkInsert,
    
    [Parameter(Mandatory = $true)]
    [string]$indexName,
    
    [Parameter(Mandatory = $true,ParameterSetName = 'Convert')]
    [Parameter(Mandatory = $true,ParameterSetName = 'Load')]
    [string]$inputFilename,
    
    [Parameter(Mandatory = $true,ParameterSetName = 'Convert')]
    [string]$outputFilename
)

Import-Module "$PSScriptRoot\ElasticSearch\Operations.psm1" -Force

if($loadBulk -or $retrieveDataset -or $createIndex) {

    $username = "elastic"
    $securePwd = ConvertTo-SecureString "bczk1THwmVIciEZYeCaq" -AsPlainText -Force
    $credential = New-Object System.Management.Automation.PSCredential ($username, $securePwd)

    add-type @"
        using System.Net;
        using System.Security.Cryptography.X509Certificates;
        public class TrustAllCertsPolicy : ICertificatePolicy {
            public bool CheckValidationResult(
             ServicePoint srvPoint, X509Certificate certificate,
             WebRequest request, int certificateProblem) {
                return true;
            }
        }
"@
    
    [System.Net.ServicePointManager]::CertificatePolicy = New-Object TrustAllCertsPolicy

    $headers = @{
            "Content-Type"="application/json; charset=utf-8";
            "OData-MaxVersion"="4.0";
            "OData-Version"="4.0";
    };


    if($loadBulk) {
        $body = Get-Content -Path "..\dataset\$inputFilename"
        Invoke-RestMethod "https://localhost:9200/$indexName/_bulk?pretty" -Method Post -ContentType 'application/x-ndjson' -InFile "..\dataset\$inputFilename" -Credential $credential
        #Invoke-RestMethod "https://localhost:9200/$indexName/_bulk?pretty" -Method Post -ContentType 'application/x-ndjson' -Body $body  -Credential $credential
    }
    elseif($createIndex) {
#            $body = @"
#{
#"mappings": {
#      "properties": {
#          "ID": {"type":"integer"},
#          "Title" :{"type":"text","analyzer": "my_greek_analyzer"},
#          "Body": {"type":"text","analyzer": "my_greek_analyzer"}
#    }
#  },
#  "settings": {
#    "analysis": {
#      "analyzer": {
#        "my_greek_analyzer": {
#          "type": "standard",
#          "max_token_length": 150,
#          "stopwords": "_greek_"
#        }
#      }
#    }
#  }
#}
#"@
        $mappings = @"
  "mappings": {
      "properties": {
          "ID": {"type":"integer"},
          "Title" :{"type":"text","analyzer": "$AnalyzerName"},
          "Body": {"type":"text","analyzer": "$AnalyzerName"}
    }
  }
"@
        if($ApplyStemming -and $RemoveStopwords) {
            Set-Index -IndexName $indexName -Mappings $mappings -RemoveStopwords -ApplyStemming -AnalyzerName $AnalyzerName -Headers $headers -Credential $credential
        }
        elseif($ApplyStemming) {
            Set-Index -IndexName $indexName -Mappings $mappings  -ApplyStemming -AnalyzerName $AnalyzerName -Headers $headers -Credential $credential
        }
        elseif($RemoveStopwords) {
            Set-Index -IndexName $indexName -Mappings $mappings -RemoveStopwords  -AnalyzerName $AnalyzerName -Headers $headers -Credential $credential
        }
        else {
            Set-Index -IndexName $indexName -IndexDefinition $IndexBodyDefinition -Headers $headers -Credential $credential
        }
    }
    else {
         $tst = Invoke-RestMethod "https://localhost:9200/dataset/_search?q= Αφρική" -Headers $headers -ContentType 'text/plain; charset=utf-8' -Method GET  -Credential $credential 
         Search -Credentials $credential
    }
}
elseif($convertJsonDocsToBulkInsert){
    
    #$inputFilename="dataset.json"
    #$index = "dataset"
    $jsonDataset = Get-Content -Path "..\dataset\$inputFilename" -Encoding UTF8 | ConvertFrom-Json
    $convertedData = ""
    foreach($doc in $jsonDataset.Documents) {
        ## Create index json entry
        if($null -ne $indexName -and $indexName.Trim() -ne "") {
            $jsonIndexEntry = '{ "index" : { "_index" : "'+$indexName+'", "_id" : "'+$doc.ID+'" } }'+"`n"
        }

        ## Create data json entry
        $firstField = $true
        $jsonDataEntry = "{"
        foreach($fieldProperty in $doc.PSObject.Properties) {
            
            $key = $fieldProperty.Name
            if($fieldProperty.TypeNameOfValue -eq "System.String"){
                $fieldProperty.Value = $fieldProperty.Value -replace '"','\"'
                $value = "`""+$fieldProperty.Value+"`""
            }
            else {
                $value = $fieldProperty.Value
            }
            
            if($firstField) {
                $jsonDataEntry += "`"$key`":$value"
                $firstField = $false
            }
            else {
                $jsonDataEntry += ",`"$key`":$value"
            }
        }
        $jsonDataEntry += "}`n"
        $convertedData += $jsonIndexEntry
        $convertedData += $jsonDataEntry
    }
    $convertedData  | Out-File "..\dataset\$outputFilename.json" -Encoding utf8 -Force
}
#$defaultEncoding = [System.Text.Encoding]::GetEncoding('ISO-8859-1')
#
#$string = 'Paulé'
#
#$utf8Bytes = [System.Text.Encoding]::UTf8.GetBytes($x)
#
#$decoded = $defaultEncoding.GetString($utf8bytes)
#
#$object = New-Object psobject -Property @{
#    Original = $x
#    Decoded  = $decoded
#}
#
#$object | Format-Table -AutoSize