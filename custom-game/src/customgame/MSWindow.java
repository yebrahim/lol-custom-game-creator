package customgame;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import win.core.Psapi;
import win.core.User32Ext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MSWindow {
    public static User32Ext UserExt = User32Ext.INSTANCE;
    public final WinDef.HWND hwnd;

    public MSWindow(WinDef.HWND handle) {
        hwnd = handle;
    }

    public static boolean isValid(MSWindow w, String processFilename) {
        String filename;
        try {
            filename = w.getProcessName();
        } catch (Exception e) {
            return false;
        }
        if (!filename.endsWith(processFilename))
            return false;
        String[] separators = new String[]{"\\", "/"};
        for (String separator : separators) {
            int index = filename.lastIndexOf(separator);
            if (index != -1) {
                filename = filename.substring(index + 1);
            }
        }
        return filename.equals(processFilename);
    }

    public static List<MSWindow> windows(String processFilename, int maxLength) {
        final List<MSWindow> windows = new ArrayList<>();
        UserExt.EnumWindows((handle, arg1) -> {
            //Skip non window objects, like browser tabs
            if (!UserExt.IsWindow(handle))
                return true;
            MSWindow w = new MSWindow(handle);
            if (isValid(w, processFilename)) {
                windows.add(w);
            }
            // return if max no. of windows was found
            return maxLength < 0 || windows.size() <= maxLength;
            //User32 provides us with window title
        }, null);
        return windows;
    }

    public static MSWindow findWindow(String processFilename) {
        final List<MSWindow> windows = windows(processFilename, 1);
        return windows.size() > 0 ? windows.get(0) : null;
    }

    public void restore() {
        UserExt.ShowWindow(hwnd, 9);
    }

    public void focus() {
        restore();
        UserExt.SetForegroundWindow(hwnd);
    }

    public Rectangle getRect() throws Exception {
        WinDef.RECT result = new WinDef.RECT();

        if (!UserExt.GetWindowRect(hwnd, result)) {
            throw new Exception("API failed to retrieve valid window rectangle!");
        }
        return new Rectangle(result.left, result.top, result.right - result.left, result.bottom - result.top);
    }

    public String getProcessName() throws Exception {
        // Refference to int that will later be filled
        IntByReference pid = new IntByReference(0);
        // This function gives pid number to the second parameter passed by refference
        UserExt.GetWindowThreadProcessId(hwnd, pid);
        // Now get handle to the process
        // 0x0400 | 0x0010 stands for reading info
        // if you pass 0 you will get error 5 which stands for access denied
        int pidVal = pid.getValue();
        HANDLE process = Kernel32.INSTANCE.OpenProcess(0x0400 | 0x0010, false, pidVal);
        if (process == null)
            throw new Exception("Winapi error: " + (Kernel32.INSTANCE.GetLastError()));
        // Prepare buffer for characters, just as you would
        // in goold 'ol C program
        char[] path = new char[150];
        DWORD buffSize = new DWORD(path.length);
        // The W at the end of the function name stands for WIDE - 2byte chars
        Psapi.INSTANCE.GetModuleFileNameExW(process, null, path, buffSize);
        // convert buffer to java string
        return new String(path).split("\0")[0];
    }

    public boolean isForeground() {
        final WinDef.HWND window = UserExt.GetForegroundWindow();
        return Objects.equals(window, this.hwnd);
    }
}
