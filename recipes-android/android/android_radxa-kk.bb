inherit gettext

SUMMARY = "Compiles the full Android for the Radxa Rock board and installs only what's needed for libhybris"
HOMEPAGE = "http://git.linux-rockchip.org/"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://kernel/COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

SRC_URI = "http://dl.radxa.com/rock_pro/source/radxa_rock_android4-4_141219.tar.gz"
PV = "kitkat"

INHIBIT_PACKAGE_STRIP = "1"
COMPATIBLE_MACHINE = "radxa-rock"
S = "${WORKDIR}/radxa_rock_android4-4/"
B = "${S}"

FILESEXTRAPATHS_prepend := "${THISDIR}/files-kk:"
SRC_URI += "file://extract-headers.sh"
SRC_URI += "file://bionic-hybris-kitkat-patch"
SRC_URI[md5sum] = "cf648a3ee9682b34d681352a7b3c95cc"
SRC_URI[sha256sum] = "e797b5ae862e94e5eee19b04b904206cb44b43b89a156e6824e2b21203bfaf94"

PROVIDES += "virtual/android-system-image"
PROVIDES += "virtual/android-headers"

do_unpack_append() {
    bb.build.exec_func('do_runme', d)
}

do_runme() {
    cd ${WORKDIR}/radxa_rock_android4-4/
    if test -f runme.sh ; then
        chmod +x runme.sh
        ./runme.sh
    fi
}

do_compile() {
    cd kernel
    unset LDFLAGS
    make rk3188_radxa_rock_kitkat_defconfig
    cd ..
    bash -c '
    . build/envsetup.sh
    lunch radxa_rock-eng '
    oe_runmake
    # patch -N -d bionic -p1 < ../bionic-hybris-kitkat-patch
    # oe_runmake
}

do_install() {
    cp -r out/target/product/rk3188/system ${D}/
    rm -rf ${D}/system/app ${D}/system/fonts ${D}/system/framework ${D}/system/media ${D}/system/preinstall ${D}/system/priv-app ${D}/system/tts

    install -d ${D}${includedir}/android
    ../extract-headers.sh . ${D}${includedir}/android 4 4 2

    install -d ${D}${libdir}/pkgconfig
    install -m 0644 ${D}${includedir}/android/android-headers.pc ${D}${libdir}/pkgconfig
    rm ${D}${includedir}/android/android-headers.pc
}

PACKAGES =+ "android-system android-headers"
FILES_android-system = "/system"
FILES_android-headers = "${libdir}/pkgconfig ${includedir}/android"
