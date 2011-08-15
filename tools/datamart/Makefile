TCLKITSH_WIN=runtime/tclkit-cli-86b1.2.exe
TCLKITSH_LINUX=runtime/tclkit-8.6b1.1
RUNTIME_WIN=runtime/tclkit-gui.exe
RUNTIME_LINUX=runtime/tclkit-8.6b1.1

win-exe: target
	rm -rf mydatamart.vfs/libext/sqlite* mydatamart.vfs/libext/tls*
	cp -r windoze/libext mydatamart.vfs 
	$(TCLKITSH_WIN) runtime/sdx.kit wrap target/mydatamart.kit
	$(TCLKITSH_WIN) runtime/sdx.kit wrap target/mydatamart.exe -runtime $(RUNTIME_WIN)

linux-exe: target
	rm -rf mydatamart.vfs/libext/sqlite* mydatamart.vfs/libext/tls*
	cp -r linux/libext mydatamart.vfs
	cp $(TCLKITSH_LINUX) ./tclkit-linux 
	$(TCLKITSH_LINUX) runtime/sdx.kit wrap target/mydatamart.kit
	$(TCLKITSH_LINUX) runtime/sdx.kit wrap target/mydatamart -runtime ./tclkit-linux
	chmod +x target/mydatamart
	rm ./tclkit-linux

target: 
	mkdir target

test:
	$(TCLKITSH_WIN) unittests/unittest.tcl -testdir unittests -verbose -pe -file "excel.test"

clean:
	rm -rf target *~
	rm -rf mydatamart.vfs/libext/sqlite* mydatamart.vfs/libext/tls*
