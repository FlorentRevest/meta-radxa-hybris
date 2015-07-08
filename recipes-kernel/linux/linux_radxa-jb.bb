require recipes-kernel/linux/linux.inc
inherit gettext

SUMMARY = "Android kernel for the Radxa Rock board"
HOMEPAGE = "http://git.linux-rockchip.org/"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

SRC_URI = "git://git.jp.linux-rockchip.org/rk3188_r-box_android4.2.2_sdk.git;branch=radxa/radxa-dev;protocol=http"
SRCREV = "${AUTOREV}"
LINUX_VERSION ?= "3.0.36+"
PV = "${LINUX_VERSION}_jellybean"
DEPENDS += "initrd-radxa"

COMPATIBLE_MACHINE = "radxa-rock"
S = "${WORKDIR}/git/kernel"
B = "${S}"

FILESEXTRAPATHS_prepend := "${THISDIR}/files-jb:"
SRC_URI += "file://defconfig"
SRC_URI[md5sum] = "cf648a3ee9682b34d681352a7b3c95cc"
SRC_URI[sha256sum] = "e797b5ae862e94e5eee19b04b904206cb44b43b89a156e6824e2b21203bfaf94"

do_install_prepend() {
    # Dummy Makefile so the make target "clean _mrproper_scripts" from kernel.bbclass works.
    # This is required since the release "daisy".
    install -d arch/arm/plat-rk/rk_pm_tests
    touch arch/arm/plat-rk/rk_pm_tests/Makefile
}
