SUMMARY = "Initramfs image to boot rootfs"
DESCRIPTION = "Provides a minimal busybox-based initramfs to boot the rootfs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

IMAGE_INSTALL = "busybox base-passwd initramfs-radxa android-tools bash"
IMAGE_FEATURES = ""
IMAGE_ROOTFS_SIZE = "8192"
export IMAGE_BASENAME = "initramfs-radxa"
IMAGE_LINGUAS = ""
IMAGE_FSTYPES_forcevariable = "cpio.gz"
KERNELDEPMODDEPEND = ""

inherit core-image
