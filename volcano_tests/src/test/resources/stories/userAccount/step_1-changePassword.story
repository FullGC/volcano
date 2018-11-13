Meta:
@author Mr Project Manager

Narrative:
Dani would like to change his password

Lifecycle:
Before:
Given dani is logged in

Scenario: A user is changing its password
When dani is changing his password to 654321
Then dani is able to log in with the user-name: dani and the password: 654321

Scenario: A user typed it's current password and is not able to create a new password
When dani is changing his password to 123
Then dani is not able to log in with the user-name: dani and the password: 123

