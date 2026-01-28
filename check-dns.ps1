# PowerShell script to check DNS propagation for indelogoods.com

Write-Host "Checking DNS records for indelogoods.com..." -ForegroundColor Cyan
Write-Host ""

# Check CNAME for www
Write-Host "CNAME record for www.indelogoods.com:" -ForegroundColor Yellow
nslookup www.indelogoods.com
Write-Host ""

# Check A record for root domain
Write-Host "A record for indelogoods.com:" -ForegroundColor Yellow
nslookup indelogoods.com
Write-Host ""

# Check TXT records
Write-Host "TXT records:" -ForegroundColor Yellow
nslookup -type=TXT indelogoods.com
Write-Host ""

Write-Host "Note: DNS propagation can take 24-48 hours" -ForegroundColor Green
