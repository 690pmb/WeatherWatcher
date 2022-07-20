Write-Host "Building App..." -ForegroundColor Cyan
mvn clean install -"DskipTests" -q

Set-Variable -Name "workspace" -Value (Get-Location | Select-Object -ExpandProperty Path)

cd docker
rm -fo *.jar 

cd $workspace
Write-Host "Copy jar" -ForegroundColor Cyan
Copy-Item -Force -Path "target\*.jar" -Destination docker
cd docker
Write-Host "Build Docker image" -ForegroundColor Cyan
docker build -t weather .
cd $workspace
Write-Host "Docker compose" -ForegroundColor Cyan
docker-compose up -d
Write-Host "APP SUCCESSFULLY DEPLOYED" -ForegroundColor Green
