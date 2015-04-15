inherit kernel
inherit gettext

SUMMARY = "Compiles the full Android for the Radxa Rock Board and installs /system, the headers and the kernel"
HOMEPAGE = "http://git.linux-rockchip.org/"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

SRC_URI = "http://dl.radxa.com/rock_pro/source/radxa_rock_android4-4_141219.tar.gz"
LINUX_VERSION ?= "3.0.36+"
LINUX_VERSION_EXTENSION ?= "-radxa-hybris"

PR = "r2"
PV = "${LINUX_VERSION}+kitkat"

COMPATIBLE_MACHINE = "radxa-hybris"
S = "${WORKDIR}/radxa_rock_android4-4/kernel"

FILESEXTRAPATHS_prepend := "${THISDIR}/files-kk:"
SRC_URI += "file://defconfig"
SRC_URI += "file://extract-headers.sh"
SRC_URI[md5sum] = "cf648a3ee9682b34d681352a7b3c95cc"
SRC_URI[sha256sum] = "e797b5ae862e94e5eee19b04b904206cb44b43b89a156e6824e2b21203bfaf94"

PROVIDES += "virtual/android-system-image"
PROVIDES += "virtual/android-headers"
PROVIDES += "virtual/kernel"

do_configure_prepend() {
    cd ${WORKDIR}/radxa_rock_android4-4/
    if test -f runme.sh ; then
        chmod +x runme.sh
        ./runme.sh
    fi
    cd kernel
}

do_compile_append() {
    oe_runmake kernel.img
    cd ..
    bash -c '
    . build/envsetup.sh
    lunch radxa_rock-eng '
    oe_runmake
}

do_install_append() {
    oe_runmake headers_install INSTALL_HDR_PATH=${D}${exec_prefix}/src/linux-${KERNEL_VERSION} ARCH=$ARCH
    cd ..
    cp -r out/target/product/rk3188/system ${D}/
    rm -rf ${D}/system/app ${D}/system/fonts ${D}/system/framework ${D}/system/media ${D}/system/preinstall ${D}/system/priv-app ${D}/system/tts

    install -d ${D}${includedir}/android
    ../extract-headers.sh . ${D}${includedir}/android 4 4 2

    install -d ${D}${libdir}/pkgconfig
    install -m 0644 ${D}${includedir}/android/android-headers.pc ${D}${libdir}/pkgconfig
    rm ${D}${includedir}/android/android-headers.pc
}

do_populate_sysroot_append() {
    oe.path.copyhardlinktree(d.expand("${D}${includedir}/android"), d.expand("${SYSROOT_DESTDIR}${includedir}/android"))
}

do_package_qa() {
}

PACKAGES =+ "kernel-headers android-system android-headers"
FILES_kernel-headers = "${exec_prefix}/src/linux*"
FILES_android-system = "/system"
FILES_android-headers = "${libdir}/pkgconfig ${includedir}/android"
