$json = Get-Content -Path "../dataset/dataset.txt" -Encoding UTF8

$queries = @()
$docs = @()

$docId = 0
$qId = 0

$relevanceRank = 0

$firstEntry = 1

foreach($entry in $json ) {

    if($entry.startsWith("q:")) {
        $docPosition = 0

        if($firstEntry -eq 0) {
            $newQuery.RelevantDocs = $relevantDocs
            $queries += $newQuery
        }
        else {
            $firstEntry = 0
        }

        $newQuery = [PSCustomObject]@{
                        ID = $qId
                        Query = ($entry.Substring(2)).TrimStart(" ")
                        RelevantDocs = $null
                    }
        $relevantDocs = @()
        $relevanceRank = 0

        $qId++
    }
    elseif($entry.startsWith("Τ:")) {
        $newDoc = [PSCustomObject]@{
                        ID = $docId
                        Title = ($entry.Substring(2)).TrimStart(" ")
                        Body = ""
                    }
        $relevantDoc = [PSCustomObject]@{
                            DocId = $docId
                            RelevanceRank = $relevanceRank
                        }
        $relevantDocs += $relevantDoc
        $relevanceRank++
    }
    elseif($entry.startsWith("Ε:")) {
        $newDoc.Body = ($entry.Substring(2)).TrimStart(" ")
        $docs += $newDoc
        $docId++
    }
}

$queries | ogv
$docs | ogv
$datasetToJson = [PSCustomObject]@{
                        Queries = $queries
                        Documents = $docs
                    }
#$datasetToJson += $queries
#$datasetToJson += $docs
$datasetToJson | ConvertTo-Json -Depth 3 | Out-File "..\dataset\dataset.json" -Encoding utf8 -Force
