const std = @import("std");

pub fn build(b: *std.Build) void {
    const target = b.standardTargetOptions(.{});
    const optimize = b.standardOptimizeOption(.{});

    const mod = b.addModule("perms", .{
        .root_source_file = b.path("main.zig"),
        .target = target,
        .optimize = optimize,
    });

    const executable = b.addExecutable(.{
        .name = "perms",
        .root_module = mod,
        .win32_manifest = b.path("perms.manifest"),
    });

    const artifact = b.addInstallArtifact(executable, .{});
    b.default_step.dependOn(&artifact.step);
}