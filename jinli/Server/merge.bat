@echo off & setlocal EnableDelayedExpansion
E:
set FromSvnUrl=svn://192.168.0.124/jinli/trunk/Server
set MergeFrom=E:\CompanyProgram\jinli_svn\trunk\Server
set ToSvnUrl=svn://192.168.0.124/jinli/branches/functional_test/server
set MergeTo=E:\CompanyProgram\jinli_svn\branches\functional_test\server

cd %MergeFrom%
svn update
svn log --stop-on-copy %FromSvnUrl% > %MergeFrom%\ServerFrom.log
for /f "skip=1 eol=-" %%i in ('type %MergeFrom%\ServerFrom.log') do (
  set TarVersion=%%i
  goto line
)
:line
echo merge target veision: %TarVersion%

cd %MergeTo%
svn update
set OldVersion=%TarVersion%
svn log --stop-on-copy %ToSvnUrl% > %MergeFrom%\ServerTo.log
for /f "skip=1 eol=-" %%i in ('type %MergeFrom%\ServerTo.log') do (
  set OldVersion=%%i
  goto line
)
:line
echo merge old veision: %OldVersion%
svn merge %FromSvnUrl% -r %OldVersion%:%TarVersion% %MergeTo%
set /a OldVersion=%OldVersion:~1%+1

svn log -r %TarVersion%:%OldVersion% %MergeFrom% > %MergeFrom%\ServerChange.log

cd %MergeTo%
svn ci --file %MergeFrom%\ServerChange.log

cd %MergeFrom%
del ServerFrom.log
del ServerTo.log
del ServerChange.log

pause