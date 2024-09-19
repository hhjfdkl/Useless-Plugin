# Overview of current goals and functionality
Playground plugin for SailPoint IIQ
Covers all basic concepts for plugins and see what I can do with them
Attempt to make a custom connector to see if we can connect a plugin to SailPoint as though it were an external application

# Outline
TODO0: 
1. *Identities* Pull info of identities from SailPoint's identity database and store it in this plugin's database
    - Install (and uninstall) database script
        * Uninstall should be removed after initial testing
    - Add unique values to these to ensure all saved values are unique in plugin-DB
        * Each identity pulled into the plugin-DB should have a value appended to ensure no identity is overwritten

TODO1:
4. Create REST endpoints for full CRUD functionality

TODO2:
5. Create a UI that displays a table showing 15 users at a time with buttons to move between pages
    - Only pull 15 that need to be displayed, and when we move to next page, pull next 15
    - Should display user ID, username, ... (TBD)
    * Here we should attempt to properly implement CSS - Check with other people to see what they did

DO LATER:
2. *Entitlements & Accounts* Pull info of entitlements from SailPoint's database
    - Seek out structure from the DB
    * Unlike identities, make plugin-DB non-redundant : Uniqueness imitating already existing ones in SailPoint
3. Associate entitlements properly with identities in plugin-DB

6. Service should be implemented to ensure the plugin is consistently pulling identities from SailPoint
    - Currently named IdentityGetAutoService.java
    * Should also have customization to allow different frequencies & different identities added per second w/ restricted range

7. Create a custom policy violation

8. **BONUS** Create a widget??
    - Try making a date/time widget maybe? A clock for the home page? Make a policy violation every time it's Wednesday after 2:30pm?

9. Make a custom task