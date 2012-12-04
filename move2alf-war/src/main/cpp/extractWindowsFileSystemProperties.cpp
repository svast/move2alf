#include <stdio.h>
#include <windows.h>
#include <tchar.h>
#include "accctrl.h"
#include "aclapi.h"
#include <time.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <iostream>
#include <string.h>
#include <string>
#include <sstream>


using namespace std;

int main(int argc, char **argv)
{
	DWORD dwRtnCode = 0;
	PSID pSidOwner = NULL;
	BOOL bRtnBool = TRUE;
	LPTSTR AcctName, DomainName;
	DWORD dwAcctName = 1, dwDomainName = 1;
	SID_NAME_USE eUse = SidTypeUnknown;
	HANDLE hFile;
	PSECURITY_DESCRIPTOR pSD = NULL;
	//char* filePath = "/testdoc.pdf";
	//int divide = 1;
	char* filePath = argv[1];

	printf(filePath);

	// Get the handle of the file object.
	hFile = CreateFile(
					  filePath,
					  GENERIC_READ,
					  FILE_SHARE_READ,
					  NULL,
					  OPEN_EXISTING,
					  FILE_ATTRIBUTE_NORMAL,
					  NULL);

	// Check GetLastError for CreateFile error code.
	if (hFile == INVALID_HANDLE_VALUE) {
			  DWORD dwErrorCode = 0;

			  dwErrorCode = GetLastError();
			  _tprintf(TEXT("CreateFile error = %d\n"), dwErrorCode);
			  return -1;
	}

	// Get the owner SID of the file.
	dwRtnCode = GetSecurityInfo(
					  hFile,
					  SE_FILE_OBJECT,
					  OWNER_SECURITY_INFORMATION,
					  &pSidOwner,
					  NULL,
					  NULL,
					  NULL,
					  &pSD);

	// Check GetLastError for GetSecurityInfo error condition.
	if (dwRtnCode != ERROR_SUCCESS) {
			  DWORD dwErrorCode = 0;

			  dwErrorCode = GetLastError();
			  _tprintf(TEXT("GetSecurityInfo error = %d\n"), dwErrorCode);
			  return -1;
	}

	// First call to LookupAccountSid to get the buffer sizes.
	bRtnBool = LookupAccountSid(
					  NULL,           // local computer
					  pSidOwner,
					  AcctName,
					  (LPDWORD)&dwAcctName,
					  DomainName,
					  (LPDWORD)&dwDomainName,
					  &eUse);

	// Reallocate memory for the buffers.
	AcctName = (char *)GlobalAlloc(
			  GMEM_FIXED,
			  dwAcctName);

	// Check GetLastError for GlobalAlloc error condition.
	if (AcctName == NULL) {
			  DWORD dwErrorCode = 0;

			  dwErrorCode = GetLastError();
			  _tprintf(TEXT("GlobalAlloc error = %d\n"), dwErrorCode);
			  return -1;
	}

		DomainName = (char *)GlobalAlloc(
			   GMEM_FIXED,
			   dwDomainName);

    // Check GetLastError for GlobalAlloc error condition.
    if (DomainName == NULL) {
          DWORD dwErrorCode = 0;

          dwErrorCode = GetLastError();
          _tprintf(TEXT("GlobalAlloc error = %d\n"), dwErrorCode);
          return -1;

    }

    // Second call to LookupAccountSid to get the account name.
    bRtnBool = LookupAccountSid(
          NULL,                   // name of local or remote computer
          pSidOwner,              // security identifier
          AcctName,               // account name buffer
          (LPDWORD)&dwAcctName,   // size of account name buffer
          DomainName,             // domain name
          (LPDWORD)&dwDomainName, // size of domain name buffer
          &eUse);                 // SID type

    // Check GetLastError for LookupAccountSid error condition.
    if (bRtnBool == FALSE) {
          DWORD dwErrorCode = 0;

          dwErrorCode = GetLastError();

          if (dwErrorCode == ERROR_NONE_MAPPED)
              _tprintf(TEXT
                  ("Account owner not found for specified SID.\n"));
          else
              _tprintf(TEXT("Error in LookupAccountSid.\n"));
          return -1;

    } else if (bRtnBool == TRUE)

        // Print the account name.
        _tprintf(TEXT("%s\n"), AcctName);


//-------------------------------------------

	//stringstream ss,sst;

	FILETIME ft,mt;
	SYSTEMTIME st,smt;

	//De System time geeft een gestandaardiseerde tijd,
	//namelijk die waarbij de tijdszone 00:00 (GMT) is.
	//Vandaar dat de "+00:00" hardcoded is.
	GetFileTime(hFile, &ft, NULL, NULL);
	FileTimeToSystemTime(&ft, &st);

	printf("%04d-%02d-%02dT%02d:%02d:%02d.%03d+%s \n",st.wYear,st.wMonth,st.wDay,st.wHour,st.wMinute,st.wSecond,st.wMilliseconds,"00:00");

	GetFileTime(hFile, NULL, NULL, &mt);
	FileTimeToSystemTime(&mt, &smt);


	printf("%04d-%02d-%02dT%02d:%02d:%02d.%03d+%s \n",smt.wYear,smt.wMonth,smt.wDay,smt.wHour,smt.wMinute,smt.wSecond,smt.wMilliseconds,"00:00");

    return 0;

}
