function Set-Index {
    param(
        $IndexName,
        [Parameter(Mandatory=$true,ParameterSetName = 'ManuallyGenerated')]
        [String]$IndexDefinition,
        $Headers,
        [PSCredential] $Credential,
        [Parameter(Mandatory=$false,ParameterSetName = 'SystemGenerated')]
        [switch]$RemoveStopwords,
        [Parameter(Mandatory=$false,ParameterSetName = 'SystemGenerated')]
        [switch]$ApplyStemming,
        [Parameter(Mandatory=$true,ParameterSetName = 'SystemGenerated')]
        [String]$AnalyzerName,
        [Parameter(Mandatory=$false,ParameterSetName = 'SystemGenerated')]
        [String]$Mappings
        
    )
    
    if($RemoveStopwords -and $ApplyStemming) {
        $settings = @"
        "settings": {
    "analysis": {
      "filter": {
        "greek_stop": {
          "type":       "stop",
          "stopwords":  "_greek_" 
        },
        "greek_lowercase": {
          "type":       "lowercase",
          "language":   "greek"
        },
        "greek_stemmer": {
          "type":       "stemmer",
          "language":   "greek"
        }
      },
      "analyzer": {
        "$AnalyzerName": {
          "tokenizer":  "standard",
          "filter": [
            "greek_lowercase",
            "greek_stop",
            "greek_stemmer"
          ]
        }
      }
    }
  }
"@
        if($null -ne $Mappings -and $Mappings.Length -gt 0) {
            $body = @"
{
    $mappings,
    $settings
}           
"@
        }
    }
    elseif($RemoveStopwords) {
        $settings = @"
        "settings": {
    "analysis": {
      "filter": {
        "greek_stop": {
          "type":       "stop",
          "stopwords":  "_greek_" 
        },
        "greek_lowercase": {
          "type":       "lowercase",
          "language":   "greek"
        }
      },
      "analyzer": {
        "$AnalyzerName": {
          "tokenizer":  "standard",
          "filter": [
            "greek_lowercase",
            "greek_stop"
          ]
        }
      }
    }
  }
"@
        if($null -ne $Mappings -and $Mappings.Length -gt 0) {
            $body = @"
{
    $mappings,
    $settings
}
            
"@
        }
    }
    elseif($ApplyStemming) {
        $settings = @"
        "settings": {
    "analysis": {
      "filter": {
        "greek_lowercase": {
          "type":       "lowercase",
          "language":   "greek"
        },
        "greek_stemmer": {
          "type":       "stemmer",
          "language":   "greek"
        }
      },
      "analyzer": {
        "$AnalyzerName": {
          "tokenizer":  "standard",
          "filter": [
            "greek_lowercase",
            "greek_stemmer"
          ]
        }
      }
    }
  }
"@
        if($null -ne $Mappings -and $Mappings.Length -gt 0) {
            $body = @"
{
    $mappings,
    $settings
}
            
"@
        }
    }
    else {
        $body = $IndexDefinition
    }

    if($body -ne $null -and $body.Length -gt 0) {
        Invoke-RestMethod "https://localhost:9200/$($IndexName.ToLower())" -Headers $Headers -Method PUT -Body $body -Credential $Credential
    }
}

function Format-RelevantDocs {
    param(
        $RelevantDocs
    )
    
    $UpdatedRelavantDocs = @()

    foreach($Document in $relevantDocs) {
        $tokenizedDocument = $Document | ConvertFrom-String
        $formattedDocument = [PSCustomObject]@{
            DocID = [int]$tokenizedDocument.P1.Split("=")[1].replace(";","")
            RelevanceRank = [int]$tokenizedDocument.P2.Split("=")[1].replace("}","")
        }
        $UpdatedRelavantDocs += $formattedDocument
    }

    return $UpdatedRelavantDocs
}

function Get-Data {
    param(
        [String]$path
    )

    $data = Get-Content -Path $path -Encoding UTF8 | ConvertFrom-Json
    return $data
}

function Get-Precision {
    param(
        $DocumentsRetrieved,
        $DocumentsExpected
    )
    if($null -eq $DocumentsRetrieved -or $null -eq $DocumentsExpected) {
        return 0;
    }

    $relevantRetrieved = 0
    $retrieved = 0
    foreach($documentRetrieved in $DocumentsRetrieved) {
        if($DocumentsExpected | Where-Object {$_.DocID -eq $documentRetrieved}) {
            $relevantRetrieved++
        }
        $retrieved++
    }

    return $relevantRetrieved / $retrieved
}

function Get-Recall {
    param(
        $DocumentsRetrieved,
        $DocumentsExpected
    )
    if($null -eq $DocumentsRetrieved -or $null -eq $DocumentsExpected) {
        return 0;
    }

    $relevantRetrieved = 0
    $retrieved = 0
    $relevant = 0
    foreach($documentRetrieved in $DocumentsRetrieved) {
        if($DocumentsExpected | Where-Object {$_.DocID -eq $documentRetrieved}) {
            $relevantRetrieved++
        }
        $retrieved++
    }

    foreach($relevantDocument in $DocumentsExpected) {
        $relevant++
    }

    return $relevantRetrieved / $relevant
}

function Get-F_Measure {
    param(
        $Precision,
        $Recall
    )
    if($Precision + $Recall -eq 0) {
        return 0;
    }
    return (2* $Precision * $Recall) / ($Precision + $Recall)
}

function Get-Fall_Out {
    param(
        $DocumentsRetrieved,
        $DocumentsExpected,
        $AllDocuments
    )
    if($null -eq $DocumentsRetrieved -or $null -eq $DocumentsExpected) {
        return 0;
    }

    $relevant = 0
    $nonRelevantRetrieved = 0
    foreach($documentRetrieved in $DocumentsRetrieved) {
        if($null -eq ($DocumentsExpected | Where-Object {$_.DocID -eq $documentRetrieved})) {
            $nonRelevantRetrieved++
        }
    }

    foreach($relevantDocument in $DocumentsExpected) {
        $relevant++
    }
    $nonRelevant = $AllDocuments.Count - $relevant
    return $nonRelevantRetrieved / $nonRelevant
}

function Search {
    
    param(
        [PSCredential] $Credentials,
        $IndexName
    )

    $pathtoParent =(get-item $PSScriptRoot ).parent.parent.FullName
    $data = Get-Data -path "$pathtoParent/dataset/dataset.json"
    $documents = $data.Documents
    $queries = $data.Queries
    Write-Host "count:"$queries.count

    $avPrecision = 0
    $avRecall = 0
    $avF_Measure = 0
    $avFall_Out = 0

    foreach($query in $queries) {
        $searchResult = Invoke-RestMethod "https://localhost:9200/$IndexName/_search?q=$($query.Query)" -Headers $headers -ContentType 'text/plain; charset=utf-8' -Method GET -Credential $Credentials
        $idsRetrieved = $searchResult.hits.hits._source.ID
        $idsExpected = Format-RelevantDocs -RelevantDocs $query.RelevantDocs
        
        $precision = Get-Precision -DocumentsRetrieved $idsRetrieved -DocumentsExpected $idsExpected
        $recall = Get-Recall -DocumentsRetrieved $idsRetrieved -DocumentsExpected $idsExpected
        $f_measure = Get-F_Measure -Precision $precision -Recall $recall
        $fall_out = Get-Fall_Out -DocumentsRetrieved $idsRetrieved -DocumentsExpected $idsExpected -AllDocuments $documents
        Write-Host "ID:$($query.ID), precision:$precision, recall:$recall, f_measure:$f_measure, fall_out:$fall_out"

        $avPrecision += $precision
        $avRecall += $Recall
        $avF_Measure += $f_measure
        $avFall_Out += $fall_out
    }

    $avPrecision /=  $queries.Count
    $avRecall /= $queries.Count
    $avF_Measure /= $queries.Count
    $avFall_Out /= $queries.Count

    Write-Host "av precision:$avPrecision, av recall:$avRecall, av f_measure:$avF_Measure, av fall_out:$avFall_Out"
}

Export-ModuleMember -Function Search
Export-ModuleMember -Function Set-Index