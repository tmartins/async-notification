async-notification
==================

Module to send notifications asynchronously

To use this plugin, you need to patch *nuxeo-user-profile* to add a new property to store the kind of subscription
* patch default plugin 'nuxeo-user-profile' with readme/nuxeo-user-profile.diff
* replace the default plugin in $NUXEO/nxserver/bundles with the patched one
