# Rendefood
##An android application for reserving a place in a restaurant
#####(Uses a local retrofit server)
The application gets the data of the restaurant from a local retrofit server (A json file)
Data is also saved in a ROOM database, but the app requires login which won't work without an internet connection (The ROOM part was a needed restriction)
Each restaurant has its menu, the user chooses the restaurant they want to go to after checking the menu
Then chooses the date and slot to go to, if it's not full then they can reserve
