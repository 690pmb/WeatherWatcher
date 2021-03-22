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
Write-Host "Copying to clipboard running script" -ForegroundColor Cyan
Set-Clipboard -Value @("cd /volume1/web/weather", "rm -f nohup.out", "ps ax | grep java | grep -v 'grep' | cut -d '?' -f1 | xargs kill -9", ("nohup java -Dserver.port=7878 -jar -Dspring.profiles.active=dev " + $jar + " &"))
