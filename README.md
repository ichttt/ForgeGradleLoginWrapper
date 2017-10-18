# ForgeGradleLoginWrapper
<h3>A dev enviroment basic login manager</h3>

This Application is intended as a launcher for ForgeGradle.

<h4>ForgeGradle isn't handling login the proper way</h4>

While ForgeGradle supports login, it is very basic and requires you to type your
username and password into the command line options.
While this is good (and recommended by FGLW) for the username, it is very insecure
in terms of password security.

<h4>How does FGLW solve this problem</h4>

This wrapper fixes the problem by asking the user via a GUI interface.
If the login is successful, a copy of the access token gets encrypted via a global key
(which is stored in your .minecraft folder), and your UUID, your username and 
encrypted access token get saved into a file in your run folder.
This way, it is very hard to steal your access token(which can be invalidated easily)
and practical impossible to steal your password, as it only exists in FGLW when renewing
the access token, and it is not passed to ForgeGradle. 
Afterwards, the access token is send to ForgeGradle, which
forwards the credentials to minecraft.