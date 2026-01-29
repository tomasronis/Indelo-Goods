# Check DNS propagation for indelogoods.com

Write-Host "`n=== Checking DNS for indelogoods.com ===`n" -ForegroundColor Cyan

# Check A record
Write-Host "A Record:" -ForegroundColor Yellow
nslookup indelogoods.com
Write-Host ""

# Check TXT record for verification
Write-Host "TXT Records (for domain verification):" -ForegroundColor Yellow
nslookup -type=TXT indelogoods.com
Write-Host ""

# Check CNAME for www
Write-Host "CNAME for www.indelogoods.com:" -ForegroundColor Yellow
nslookup www.indelogoods.com
Write-Host ""

Write-Host "=== Expected Values ===" -ForegroundColor Green
Write-Host "A Record: Should point to 20.48.204.10"
Write-Host "TXT Record: Should contain Azure verification ID (asuid.indelogoods.com)"
Write-Host ""
Write-Host "Note: DNS propagation can take 24-48 hours" -ForegroundColor Yellow
