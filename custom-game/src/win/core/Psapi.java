package win.core;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.W32APIOptions;

public interface Psapi extends WinNT {
    Psapi INSTANCE = (Psapi) Native.loadLibrary("Psapi.dll", Psapi.class, W32APIOptions.DEFAULT_OPTIONS);

    void GetModuleFileNameExW(HANDLE hProcess, HMODULE hModule, char[] pathName, DWORD pathNameSize);
}