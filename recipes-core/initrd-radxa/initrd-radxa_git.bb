# In doubt, specify closed-source. But nothing is specified
LICENSE = "CLOSED"
SRC_URI = "git://github.com/radxa/initrd.git;destsuffix=${WORKDIR}/initrd"
SRCREV_pn-${PN} = "${AUTOREV}"

do_deploy_append() {
    install -d ${DEPLOYDIR}
    cd ${WORKDIR}/initrd
    find . ! -path "*/.git*" ! -path "./README.md" ! -path "./Makefile" \
    | cpio -H newc -ov > ${DEPLOYDIR}/initrd.img
}
