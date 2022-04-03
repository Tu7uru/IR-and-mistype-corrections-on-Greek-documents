param(
    [Parameter(Mandatory = $true,ParameterSetName = 'Load')]
    [switch]$loadBulk,
    [Parameter(Mandatory = $true,ParameterSetName = 'Convert')]
    [switch]$convertJsonDocsToBulkInsert,
#    [Parameter(Mandatory = $true,ParameterSetName = 'Convert')]
    [string]$index,
#    [Parameter(Mandatory = $true,ParameterSetName = 'Convert')]
    [string]$inputFilename,
    [Parameter(Mandatory = $true,ParameterSetName = 'Convert')]
    [string]$outputFilename
)

if($loadBulk) {

    $username = "elastic"
    $securePwd = ConvertTo-SecureString "s+bomWnaC=oMdfXvAeSr" -AsPlainText -Force
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
    Invoke-RestMethod "https://localhost:9200/$index/_bulk?pretty" -Method Post -ContentType 'application/x-ndjson' -InFile "..\$index\$inputFilename" -Credential $credential
    #Invoke-RestMethod "https://localhost:9200" -Method GET -Credential $credential
}
elseif($convertJsonDocsToBulkInsert){
    
    #$inputFilename="dataset.json"
    #$index = "dataset"
    $jsonDataset = Get-Content -Path "..\dataset\$inputFilename" -Encoding UTF8 | ConvertFrom-Json
    $ConvertedData = ""
    foreach($doc in $jsonDataset.Documents) {
        ## Create index json entry
        if($null -ne $index -and $index.Trim() -ne "") {
            $jsonIndexEntry = '{ "index" : { "_index" : "'+$index+'", "_id" : "'+$doc.ID+'" } }'+"`n"
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
        $ConvertedData += $jsonIndexEntry
        $ConvertedData += $jsonDataEntry
    }
    $ConvertedData  | Out-File "..\dataset\$outputFilename.json" -Encoding utf8 -Force
}