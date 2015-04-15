inherit kernel
inherit gettext

SUMMARY = "Compiles the full Android for the Radxa Rock Board and installs /system, the headers and the kernel"
HOMEPAGE = "http://git.linux-rockchip.org/"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

SRC_URI = "git://git.us.linux-rockchip.org/rk3188_r-box_android4.2.2_sdk.git;branch=radxa/radxa-dev"
SRCREV = "${AUTOREV}"
LINUX_VERSION ?= "3.4"
LINUX_VERSION_EXTENSION ?= "-radxa-hybris"

PR = "r1"
PV = "${LINUX_VERSION}+git${SRCPV}"

COMPATIBLE_MACHINE = "radxa-hybris"
S = "${WORKDIR}/git/kernel"

FILESEXTRAPATHS_prepend := "${THISDIR}/files-jb:"
SRC_URI += "file://defconfig"

PROVIDES += "virtual/android-system-image"
PROVIDES += "virtual/android-headers"
PROVIDES += "virtual/kernel"

do_compile_append() {
    oe_runmake kernel.img
    cd ..
    bash -c '
    . build/envsetup.sh
    lunch rk31sdk-eng '
    oe_runmake
}

do_install_append() {
    oe_runmake headers_install INSTALL_HDR_PATH=${D}${exec_prefix}/src/linux-${KERNEL_VERSION} ARCH=$ARCH
    cd ..
    install -d ${D}/system/
    install -d out/target/product/rk31sdk/system ${D}/system/
}

PACKAGES =+ "kernel-headers"
FILES_kernel-headers = "${exec_prefix}/src/linux*"
