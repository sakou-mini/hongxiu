@echo off

for /f  %%i in (version.txt) do (set version=%%i)
echo local version is : %version%

for /f %%i in ('git describe --tags') do ( set latestVersion=%%i)
echo git version is :%latestVersion%

if %version% leq %latestVersion% ( echo %version%  not greater than gitVersion ,not push tag) ^
else ( echo version is pass
    git tag -a %version% -m "auto git tag"
	git push origin %version%
	echo push tag push success!
)




