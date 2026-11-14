const std = @import("std");

pub fn main(init: std.process.Init) !void {
    var result = try std.process.spawn(init.io, .{
        .argv = &[_][]const u8{ "icacls", "C:\\Program Files\\EA Games\\ItTakesTwo", "/grant", "*S-1-5-32-545:(OI)(CI)F", "/t" },
        .stderr = .ignore,
        .stdin = .ignore,
    });
    _ = try result.wait(init.io);
}