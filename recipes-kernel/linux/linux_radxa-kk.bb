require recipes-kernel/linux/linux.inc
inherit gettext

SUMMARY = "Android kernel for the Radxa Rock board"
HOMEPAGE = "http://git.linux-rockchip.org/"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

SRC_URI = "http://dl.radxa.com/rock_pro/source/radxa_rock_android4-4_141219.tar.gz"
LINUX_VERSION ?= "3.0.36+"
PV = "${LINUX_VERSION}_kitkat"

COMPATIBLE_MACHINE = "radxa-rock"
S = "${WORKDIR}/radxa_rock_android4-4/kernel"
B = "${S}"

FILESEXTRAPATHS_prepend := "${THISDIR}/files-kk:"
SRC_URI += "file://defconfig"
SRC_URI[md5sum] = "cf648a3ee9682b34d681352a7b3c95cc"
SRC_URI[sha256sum] = "e797b5ae862e94e5eee19b04b904206cb44b43b89a156e6824e2b21203bfaf94"

do_unpack_append() {
    bb.build.exec_func('do_runme', d)
}

do_runme() {
    cd ${WORKDIR}/radxa_rock_android4-4/
    if test -f runme.sh ; then
        chmod +x runme.sh
        ./runme.sh
    fi
    cd kernel
}

# do_compile_append() {
#     oe_runmake kernel.img
# }

do_install_prepend() {
    # Dummy Makefile so the make target "clean _mrproper_scripts" from kernel.bbclass works.
    # This is required since the release "daisy".
    install -d arch/arm/plat-rk/rk_pm_tests
    touch arch/arm/plat-rk/rk_pm_tests/Makefile
}
