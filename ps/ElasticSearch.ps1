param(
    [switch]$loadBulk
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
    #Invoke-RestMethod "https://localhost:9200/dataset/_bulk?pretty" -Method Post -ContentType 'application/x-ndjson' -InFile "..\dataset\dataset.json"
    Invoke-RestMethod "https://localhost:9200" -Method GET -Credential $credential
}