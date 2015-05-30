# In doubt, specify closed-source. But nothing is specified
LICENSE = "CLOSED"
SRC_URI = "git://github.com/radxa/initrd.git;destsuffix=${WORKDIR}/initrd"
SRCREV_pn-${PN} = "${AUTOREV}"

inherit deploy

do_deploy() {
    install -d ${DEPLOYDIR}
    cd ${WORKDIR}/initrd
    find . ! -path "*/.git*" ! -path "./README.md" ! -path "./Makefile" \
    | cpio -H newc -ov > ${DEPLOYDIR}/initrd.img
}

addtask deploy before do_build after do_compile
