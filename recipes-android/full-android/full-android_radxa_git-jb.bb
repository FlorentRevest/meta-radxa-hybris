inherit kernel
inherit gettext

SUMMARY = "Compiles the full Android for the Radxa Rock Board and installs /system, the headers and the kernel"
HOMEPAGE = "http://git.linux-rockchip.org/"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

SRC_URI = "git://git.jp.linux-rockchip.org/rk3188_r-box_android4.2.2_sdk.git;branch=radxa/radxa-dev;protocol=http"
SRCREV = "${AUTOREV}"
LINUX_VERSION ?= "3.0.36+"
LINUX_VERSION_EXTENSION ?= "-radxa-hybris"

PR = "r1"
PV = "${LINUX_VERSION}+jellybean"

INHIBIT_PACKAGE_STRIP = "1"
COMPATIBLE_MACHINE = "radxa-hybris"
S = "${WORKDIR}/git/kernel"
B = "${WORKDIR}/git/kernel"

FILESEXTRAPATHS_prepend := "${THISDIR}/files-jb:"
SRC_URI += "file://defconfig"
SRC_URI += "file://extract-headers.sh"
SRC_URI += "file://bionic-hybris-jellybean.patch-android"
SRC_URI[md5sum] = "cf648a3ee9682b34d681352a7b3c95cc"
SRC_URI[sha256sum] = "e797b5ae862e94e5eee19b04b904206cb44b43b89a156e6824e2b21203bfaf94"

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
    #patch -d bionic -p1 < ../bionic-hybris-jellybean.patch-android
    #oe_runmake
}

do_install_prepend() {
    # Dummy Makefile so the make target "clean _mrproper_scripts" from kernel.bbclass works.
    # This is required since the release "daisy".
    install -d arch/arm/plat-rk/rk_pm_tests
    touch arch/arm/plat-rk/rk_pm_tests/Makefile
}

do_install_append() {
    oe_runmake headers_install INSTALL_HDR_PATH=${D}${exec_prefix}/src/linux-${KERNEL_VERSION} ARCH=$ARCH
    cd ..
    cp -r out/target/product/rk31sdk/system ${D}/system/
    rm -rf ${D}/system/app ${D}/system/fonts ${D}/system/framework ${D}/system/media ${D}/system/preinstall ${D}/system/priv-app ${D}/system/tts

    install -d ${D}${includedir}/android
    ../extract-headers.sh . ${D}${includedir}/android 4 2 2

    install -d ${D}${libdir}/pkgconfig
    install -m 0644 ${D}${includedir}/android/android-headers.pc ${D}${libdir}/pkgconfig
    rm ${D}${includedir}/android/android-headers.pc
}

do_populate_sysroot_append() {
    oe.path.copyhardlinktree(d.expand("${D}${includedir}/android"), d.expand("${SYSROOT_DESTDIR}${includedir}/android"))
    oe.path.copyhardlinktree(d.expand("${D}/usr/lib/pkgconfig"), d.expand("${SYSROOT_DESTDIR}/usr/lib/pkgconfig"))
}

do_package_qa() {
}

PACKAGES =+ "kernel-headers android-system android-headers"
FILES_kernel-headers = "${exec_prefix}/src/linux*"
FILES_android-system = "/system"
FILES_android-headers = "${libdir}/pkgconfig ${includedir}/android"
