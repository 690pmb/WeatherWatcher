Write-Host "Building App.." -ForegroundColor Cyan
mvn clean install -"Dspring-boot.run.profiles='dev'" -"Dmaven.test.skip=true" -q
Write-Host "Deploying App" -ForegroundColor Cyan

Set-Variable -Name "web" -Value "\\myNas\web\Weather"
Set-Variable -Name "workspace" -Value (Get-Location | Select-Object -ExpandProperty Path)

cd $web
rm -r -fo *.jar 

cd $workspace
[xml]$pom = Get-Content pom.xml
$jar = "weather-watcher-" + $pom.project.version + ".jar"
Copy-Item -Force -Path ("target\" + $jar) -Destination $web
Write-Host "APP SUCCESSFULLY DEPLOYED" -ForegroundColor Green
Write-Host "Starting App..." -ForegroundColor Cyan
cd $web
java -"Dserver.port=7878" -jar -"Dspring.profiles.active=dev" $jar
