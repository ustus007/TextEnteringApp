This project uses https://github.com/kwhat/jnativehook library. It is shared under its own license.

1) Project only works with symbols available with usage of USA QWERTY keyboard layout.
2) It's intended usage is to enter text into VMs/remote desktops that do not share a clipboard and have some restrictions of other means for text input. So, for correctness of text entering, keyboard layout on target system should be compatible with USA QWERTY (but not restricted to it, for example, Polish programming keyboard layout also works).
3) It should be run on Windows, but operating system on target VM/remote desktop is not important
4) Some applications can add their own logic while text entering (keeping tabulations, for example). To avoid that, usage of most basic text editing application (like Notepad on Windows) as a buffer is recommended.
5) After pressing "Enter text" button, 5 seconds delay is given before start of entering text to give time for selection of appropriate window.
6) Pressing F12 stops text entering. 
