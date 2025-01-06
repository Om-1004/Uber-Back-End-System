// Om Patel
// Student ID: 501170337

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/*
 * 
 * This class contains the main logic of the system.
 * 
 *  It keeps track of all users, drivers and service requests (RIDE or DELIVERY)
 * 
 */
public class TMUberSystemManager
{
  private ArrayList<User>   users;
  private ArrayList<Driver> drivers;

  private ArrayList<TMUberService> serviceRequests; 

  public double totalRevenue; // Total revenues accumulated via rides and deliveries
  
  // Rates per city block
  private static final double DELIVERYRATE = 1.2;
  private static final double RIDERATE = 1.5;
  // Portion of a ride/delivery cost paid to the driver
  private static final double PAYRATE = 0.1;

  //These variables are used to generate user account and driver ids
  int userAccountId = 900;
  int driverId = 700;

  public TMUberSystemManager()
  {
    users   = new ArrayList<User>();
    drivers = new ArrayList<Driver>();
    serviceRequests = new ArrayList<TMUberService>(); 
    
    TMUberRegistered.loadPreregisteredUsers(users);
    TMUberRegistered.loadPreregisteredDrivers(drivers);
    
    totalRevenue = 0;
  }

  // General string variable used to store an error message when something is invalid 
  // (e.g. user does not exist, invalid address etc.)  
  // The methods below will set this errMsg string and then return false
  String errMsg = null;

  public String getErrorMessage()
  {
    return errMsg;
  }
  
  // Given user account id, find user in list of users
  // Return null if not found
  public User getUser(String accountId)
  {
    // Fill in the code
    for (int i  = 0; i < users.size(); i++){
      if(users.get(i).getAccountId().equals(accountId)){
        return users.get(i);
      }
    }
    return null;
  }
  
  // Check for duplicate user
  private boolean userExists(User user)
  {
    for (int i  = 0; i < users.size(); i++){
      if(users.get(i).equals(user)){
        return true;
      }
    }
    // Fill in the code
    return false;
  }
  
 // Check for duplicate driver
 private boolean driverExists(Driver driver)
 {
   // Fill in the code
   for (int i  = 0; i < drivers.size(); i++){
    if(drivers.get(i).equals(driver)){
      return true;
    }
  }
   return false;
 }
  
  // Given a user, check if user ride/delivery request already exists in service requests
  private boolean existingRequest(TMUberService req) {
    for (int i = 0; i < serviceRequests.size(); i++) {
        if (serviceRequests.get(i).getServiceType().equals(req.getServiceType())) { // Checking if the service requests type is the same as the requests 
            if (serviceRequests.get(i).equals(req)) { // Check if the two requests are equal to each other by using the .equals method created
                return true;
            }
        }
    }
    return false;
}


  // Calculate the cost of a ride or of a delivery based on distance 
  private double getDeliveryCost(int distance)
  {
    return distance * DELIVERYRATE;
  }

  private double getRideCost(int distance)
  {
    return distance * RIDERATE;
  }

  // Go through all drivers and see if one is available
  // Choose the first available driver
  // Return null if no available driver
  private Driver getAvailableDriver()
  {
    // Fill in the code
    for (int i  = 0; i < drivers.size(); i++){
      if(drivers.get(i).getStatus() == Driver.Status.AVAILABLE){
        return drivers.get(i);
      }
    }
    return null;
  }

  // Print Information (printInfo()) about all registered users in the system
  public void listAllUsers()
  {
    System.out.println();
    
    for (int i = 0; i < users.size(); i++)
    {
      int index = i + 1;
      System.out.printf("%-2s. ", index);
      users.get(i).printInfo();
      System.out.println(); 
    }
  }

  // Print Information (printInfo()) about all registered drivers in the system
  public void listAllDrivers()
  {
    for (int i  = 0; i < drivers.size(); i++){
      System.out.print(i+1 + ". ");
      drivers.get(i).printInfo();
      System.out.println("");
    }
  }

  // Print Information (printInfo()) about all current service requests
  public void listAllServiceRequests()
  {
    // Fill in the code
    for (int i = 0; i < serviceRequests.size(); i++){
      System.out.println("");
      System.out.print(i+1 + ". ------------------------------------------------------------");
      serviceRequests.get(i).printInfo();
      System.out.println("");
    }
  }

  // Add a new user to the system
  public boolean registerNewUser(String name, String address, double wallet)
  {
    // Fill in the code. Before creating a new user, check paramters for validity

    if (name == null || name.equals("")){
      errMsg = "Invalid User Name";
      return false;
    }

    if (address == null || !(CityMap.validAddress(address))){
      errMsg = "Invalid User Address";
      return false;
    }

    if (wallet < 0.0){
      errMsg = "Invalid Money in Wallet";
      return false;
    }


    User newUser = new User(TMUberRegistered.generateUserAccountId(users), name, address, wallet); // Creates a new User if the there are no invalid input
    if(userExists(newUser)){ // Check if the user exists and displays the message using the .errorMessage() in TMUberUI
      errMsg = "User Already Exists in System";
      return false;
    }
    users.add(newUser);
    return true;
    
    // See the assignment document for list of possible erros that might apply
    // Write the code like (for example):
    // if (address is *not* valid)
    // {
    //    set errMsg string variable to "Invalid Address "
    //    return false
    // }
    // If all parameter checks pass then create and add new user to array list users
    // Make sure you check if this user doesn't already exist!
    //return false;
  }

  // Add a new driver to the system
  public boolean registerNewDriver(String name, String carModel, String carLicencePlate)
  {
    
    // Fill in the code - see the assignment document for error conditions


    if (name == null || name.equals("")){
      errMsg = "Invalid Driver Name";
      return false;
    }

    if (carModel == null || carModel.equals("")){
      errMsg = "Invalid Car Model";
      return false;
    }

    if (carLicencePlate == null || carLicencePlate.equals("")){
      errMsg = "Invalid Car Licence Plate";
      return false;
    }

    Driver newDriver = new Driver(TMUberRegistered.generateDriverId(drivers), name, carModel, carLicencePlate); // Creates a new driver if there are no invalid inputs
    if(driverExists(newDriver)){ // Check if the driver exists and displays the message using the .errorMessage() in TMUberUI
      errMsg = "Driver Already Exists in System";
      return false;
    }
    drivers.add(newDriver);
    return true;

    // that might apply. See comments above in registerNewUser
  }

  // Request a ride. User wallet will be reduced when drop off happens
  public boolean requestRide(String accountId, String from, String to)
  {

    if (!(CityMap.validAddress(from))){
      errMsg = "Invalid User Address";
      return false;
    }

    if (!(CityMap.validAddress(to))){
      errMsg = "Invalid Address";
      return false;
    }
    
    Driver availD = getAvailableDriver();
    if (availD == null) {
        errMsg = "No Drivers Available";
        return false;
    }
    User user_requested = getUser(accountId);
    if (user_requested == null ){
      errMsg = "User Account Not Found";
      return false;
    }

        
    int distance = CityMap.getDistance(from, to);
    if(distance <= 1){
      errMsg = "Insufficient Travel Distance";  
      return false;
    }   

    
    TMUberRide ride = new TMUberRide(availD, from, to, user_requested, distance, getRideCost(distance)); // Creates a ride request if there are no invalid inputs

    if(existingRequest(ride)){
      errMsg = "User Already Has Ride Request";
      return false;
    }

    if (user_requested.getWallet() < getRideCost(distance)){
      errMsg = "Insufficient Funds";
      return false;
    }

    availD.setStatus(Driver.Status.DRIVING); // Setting the driver to driving
    serviceRequests.add(ride);
    user_requested.addRide();


    // Check for valid parameters
  // Use the account id to find the user object in the list of users
    // Get the distance for this ride
    // Note: distance must be > 1 city block!
    // Find an available driver
    // Create the TMUberRide object
    // Check if existing ride request for this user - only one ride request per user at a time!
    // Change driver status
    // Add the ride request to the list of requests
    // Increment the number of rides for this user
    return true;
  }

  // Request a food delivery. User wallet will be reduced when drop off happens
  public boolean requestDelivery(String accountId, String from, String to, String restaurant, String foodOrderId)
  {
    // See the comments above and use them as a guide
    // For deliveries, an existing delivery has the same user, restaurant and food order id
    // Increment the number of deliveries the user has had


    if (!(CityMap.validAddress(from))){
      errMsg = "Invalid Address";
      return false;
    }

    if (!(CityMap.validAddress(to))){
      errMsg = "Invalid User Address";
      return false;
    }

    if (restaurant == null || restaurant == ""){
      errMsg = "Invalid Address";
      return false;
    }

    if (foodOrderId == null || foodOrderId == ""){
      errMsg = "Invalid Food Order ID";
      return false;
    }

    User user_requested = getUser(accountId);
    if (user_requested == null ){ 
      errMsg = "User Account Not Found";
      return false;
    }




    int distance = CityMap.getDistance(from, to);
    if(distance <= 1){
      errMsg = "Insufficient Travel Distance";  
      return false;
    }   

    Driver availD = getAvailableDriver();
    if (availD == null) {
        errMsg = "No Drivers Available";
        return false;
    }
    
    TMUberDelivery deliveryReq = new TMUberDelivery(availD, from, to, user_requested, distance, getDeliveryCost(distance), restaurant, foodOrderId); // Created a delivery request if there are no invalid inputs


    if (user_requested.getWallet() < getDeliveryCost(distance)){ // Checks if user wallet is less than the delivery cost
      errMsg = "Insufficient Funds";
      return false;
    }


    if(existingRequest(deliveryReq)){ // Checks if the delivery exists or not
      errMsg = "User Already Has Delivery Request at Restaurant with this Food Order";
      return false;
    }

    availD.setStatus(Driver.Status.DRIVING); // Sets the driver status to driving
    serviceRequests.add(deliveryReq);
    user_requested.addDelivery();
    return true;
  }


  // Cancel an existing service request. 
  // parameter int request is the index in the serviceRequests array list
  public boolean cancelServiceRequest(int request)
  {
    // Check if valid request #
    if (request > serviceRequests.size()|| request < 0){ // Checks if the request index value is invalid
      errMsg = "Invalid Request #";
      return false;
    }
    // Remove request from list

    if(request == 0){ // Check if requests equals 0, and set it to 1 to account for the edge case 0
      request = 1;
    }

    String typeOfReq = serviceRequests.get(request-1).getServiceType();  // Gets the type of delivery request

    if (typeOfReq.equals("RIDE")){
      serviceRequests.get(request-1).getUser().removeRides();
    }

    else if (typeOfReq.equals("DELIVERY")){
      serviceRequests.get(request-1).getUser().removeDelivery();

    }
    serviceRequests.remove(request-1);

    Driver availD = getAvailableDriver();
    if (availD == null) {
        errMsg = "No Drivers Available";
        return false;
    }

    availD.setStatus(Driver.Status.AVAILABLE); // Sets the driver status to available


    // Also decrement number of rides or number of deliveries for this user
    // since this ride/delivery wasn't completed
    return true;
  }
  
  // Drop off a ride or a delivery. This completes a service.
  // parameter request is the index in the serviceRequests array list
  public boolean dropOff(int request)
  {
    // See above method for guidance
    // Get the cost for the service and add to total revenues

    if (request == 0){ // Check if requests equals 0, and set it to 1 to account for the edge case 0
      request = 1;
    }
    if (request > serviceRequests.size() || request < 0){
      errMsg = "Invalid Request #";
      return false;
    }

    String typeOfReq = serviceRequests.get(request-1).getServiceType(); // Gets the service type
    User reqU = serviceRequests.get(request-1).getUser();
    Driver payD = serviceRequests.get(request-1).getDriver();

    double costPerService = 0;
    if (typeOfReq.equals("RIDE")){
      costPerService = getRideCost(serviceRequests.get(request-1).getDistance()); 
      totalRevenue += costPerService; // Added the cost per service
      reqU.payForService(costPerService); // Deducted the cost per service from user
      
    }
    else if (typeOfReq.equals("DELIVERY")){
      costPerService = getDeliveryCost(serviceRequests.get(request-1).getDistance());
      totalRevenue += costPerService; // Added the cost per service
      reqU.payForService(costPerService);// Deducted the cost per service from user
    }



   
    payD.pay(PAYRATE * costPerService); // Pay the driver
    totalRevenue -= PAYRATE * costPerService;
    serviceRequests.remove(request-1);

    // Pay the driver
    // Deduct driver fee from total revenues
    // Change driver status
    payD.setStatus(Driver.Status.AVAILABLE); // Set the driver status to available
    // Deduct cost of service from user
    return true;
  }


  // Sort users by name
  // Then list all users
  public void sortByUserName()
  {

    // Collection.sort() takes in two parameters
    // 1st parameter: What needs to be sorted
    // 2nd parameter: An object that implements comparator

    Collections.sort(users, new NameComparator()); // Sorts the user based on the custom comparator we made, NameComparator
    listAllUsers();

  }

  // Helper class for method sortByUserName
  private class NameComparator implements Comparator<User> // Implements the comparator interface for User Objects 
  {
    @Override
    public int compare(User user1, User user2){ // Compares the two users and returns -1, 0 or 1
      return user1.getName().compareTo(user2.getName()); // Checks lexographically for the two user's name
    }
    
  }

  // Sort users by number amount in wallet
  // Then list all users
  public void sortByWallet()
  {
    Collections.sort(users, new UserWalletComparator()); // Sorts the user's list based on the custom comparator interface we made, UseWalletComparator
    listAllUsers();

  }
  // Helper class for use by sortByWallet
  private class UserWalletComparator implements Comparator<User> // Implements the comparator interface for User Objects
  {
    @Override
    public int compare(User user1, User user2){ // Compares the two user and returns -1, 0 or 1
      return Double.compare(user1.getWallet(), user2.getWallet()); // Compares the wallet balance of user1 and user 2 

    }
  }

  // Sort trips (rides or deliveries) by distance
  // Then list all current service requests
  public void sortByDistance() {
    Collections.sort(serviceRequests); // Sorts the requests based on the distance value
    listAllServiceRequests();
}


}