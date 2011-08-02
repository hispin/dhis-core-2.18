#!./tclkit-cli-86b1.2.exe

package require tcltest

eval ::tcltest::configure $argv
::tcltest::runAllTests
::tcltest::cleanupTests
