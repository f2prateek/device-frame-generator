# Imports the monkeyrunner modules used by this program
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice

# Connects to the current device, returning a MonkeyDevice object
device = MonkeyRunner.waitForConnection()

# Installs the Android package. Notice that this method returns a boolean, so you can test
# to see if the installation worked.
device.installPackage('../app/target/com-f2prateek-dfg-1.0.apk')

# sets a variable with the package's internal name
package = 'com.f2prateek.dfg'

# sets a variable with the name of an Activity in the package
activity = 'com.f2prateek.dfg.ui.CarouselActivity'

# sets the name of the component to start
runComponent = package + '/' + activity

# Runs the component
device.startActivity(component=runComponent)

MonkeyRunner.sleep(5)

device.type('example@example.com')

# Takes a screenshot
result = device.takeSnapshot()

# Writes the screenshot to a file
result.writeToFile('screenshot.png','png')
