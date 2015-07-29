inherit gettext

SUMMARY = "Compiles the full Android for the Radxa Rock board and installs only what's needed for libhybris"
HOMEPAGE = "http://git.linux-rockchip.org/"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://kernel/COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

SRC_URI = "git://git.jp.linux-rockchip.org/rk3188_r-box_android4.2.2_sdk.git;branch=radxa/radxa-dev;protocol=http"
SRCREV = "${AUTOREV}"
PV = "jellybean"

INHIBIT_PACKAGE_STRIP = "1"
COMPATIBLE_MACHINE = "rk3188-radxarock"
S = "${WORKDIR}/git/"
B = "${S}"

SRC_URI += "file://extract-headers.sh"
SRC_URI += "file://bionic-hybris-jellybean-patch"
SRC_URI[md5sum] = "cf648a3ee9682b34d681352a7b3c95cc"
SRC_URI[sha256sum] = "e797b5ae862e94e5eee19b04b904206cb44b43b89a156e6824e2b21203bfaf94"

PROVIDES += "virtual/android-system-image"
PROVIDES += "virtual/android-headers"

do_compile() {
    cd kernel
    unset LDFLAGS
    make rk3188_radxa_rock_defconfig
    make kernel.img
    cd ..
    bash -c '
    . build/envsetup.sh
    lunch rk31sdk-eng '
    oe_runmake

    patch -d bionic -p1 < ../bionic-hybris-jellybean-patch
    oe_runmake
}

do_install() {
    cp -r out/target/product/rk31sdk/system ${D}/system/
    rm -rf ${D}/system/app ${D}/system/fonts ${D}/system/framework ${D}/system/media ${D}/system/preinstall ${D}/system/priv-app ${D}/system/tts

    install -d ${D}${includedir}/android
    ../extract-headers.sh . ${D}${includedir}/android 4 2 2

    install -d ${D}${libdir}/pkgconfig
    install -m 0644 ${D}${includedir}/android/android-headers.pc ${D}${libdir}/pkgconfig
    rm ${D}${includedir}/android/android-headers.pc

    sed -i "s/rk31sdk/rk30board/g" /${D}/system/build.prop
    install -d ${D}/lib/modules/3.0.36+/
    install -d ${D}/etc/modules-load.d/
    ln -s ../../../system/lib/modules/ump.ko.3.0.36+  ${D}/lib/modules/3.0.36+/ump.ko
    ln -s ../../../system/lib/modules/mali.ko.3.0.36+ ${D}/lib/modules/3.0.36+/mali.ko
    echo -e "ump\nmali\n" > ${D}/etc/modules-load.d/mali-android.conf
}

FILES_${PN} = "/system ${libdir}/pkgconfig ${includedir}/android /etc /lib"
