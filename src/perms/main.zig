// Helper program for the it takes two launcher
// Handles any functionality that requires administrator privelages.

const std = @import("std");
const windows = std.os.windows;

pub fn main(init: std.process.Init) !u8 {
    const args = try init.minimal.args.toSlice(init.arena.allocator());
    defer init.arena.allocator().free(args);

    var gamepath: ?[:0]const u8 = null;
    defer if(gamepath) |p| init.gpa.free(p);

    for (args, 0..) |arg, i| {
        if (std.mem.eql(u8, arg, "--gamepath")) {
            // Check if the next argument exists to serve as the value
            if (i + 1 < args.len) {
                gamepath = try std.mem.joinZ(init.gpa, " ", args[i + 1..]);
            }
            break;
        }
    }

    if(gamepath) |gp| {
        // Add gamepath to windows registry.
        const path = try std.unicode.utf8ToUtf16LeAllocZ(init.gpa, gp);
        defer init.gpa.free(path);

        const HKEY_LOCAL_MACHINE: windows.HKEY = @ptrFromInt(0x80000002); // 0x80000002 is HKLM
        const KEY_WRITE = 0x0002;
        const REG_SZ = 1;

        const subkey = try std.unicode.utf8ToUtf16LeAllocZ(init.gpa, "SOFTWARE\\Hazelight\\ItTakesTwo");
        defer init.gpa.free(subkey);
        const valuename = try std.unicode.utf8ToUtf16LeAllocZ(init.gpa, "Install Dir");
        defer init.gpa.free(valuename);

        var hkey: ?windows.HKEY = null;

        const create_result = RegCreateKeyExW(HKEY_LOCAL_MACHINE, subkey.ptr, 0, null, 0, KEY_WRITE, null, &hkey, null);
        if(create_result != 0) {
            return error.RegistryCreateFailed;
        }
        defer if(hkey) |key| {
            _ = RegCloseKey(key);
        };

        const data_size: u32 = @intCast((path.len + 1) * @sizeOf(u16));
        const data_ptr: [*]const u8 = @ptrCast(path.ptr);

        const set_result = RegSetValueExW(hkey.?, valuename.ptr, 0, REG_SZ, data_ptr, data_size);
        if(set_result != 0) {
            return error.RegistrySetValueFailed;
        }
    } else {
        // Remove admin requirement from game folders.
        var result = try std.process.spawn(init.io, .{
            .argv = &[_][]const u8{ "icacls", "C:\\Program Files\\EA Games\\ItTakesTwo", "/grant", "*S-1-5-32-545:(OI)(CI)F", "/t" },
            .stderr = .ignore,
            .stdin = .ignore,
        });
        _ = try result.wait(init.io);
    }

    return 0;
}

// Extern declarations for advapi32 registry functions

extern "advapi32" fn RegCreateKeyExW(
    hKey: windows.HKEY,
    lpSubKey: [*:0]const u16,
    Reserved: u32,
    lpClass: ?[*:0]const u16,
    dwOptions: u32,
    samDesired: u32,
    lpSecurityAttributes: ?*anyopaque,
    phkResult: *?windows.HKEY,
    lpdwDisposition: ?*u32,
) callconv(.winapi) windows.LONG;

extern "advapi32" fn RegSetValueExW(
    hKey: windows.HKEY,
    lpValueName: ?[*:0]const u16,
    Reserved: u32,
    dwType: u32,
    lpData: [*]const u8,
    cbData: u32,
) callconv(.winapi) windows.LONG;

extern "advapi32" fn RegCloseKey(
    hKey: windows.HKEY,
) callconv(.winapi) windows.LONG;