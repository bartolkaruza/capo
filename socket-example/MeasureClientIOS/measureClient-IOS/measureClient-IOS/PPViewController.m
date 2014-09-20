//
//  PPViewController.m
//  measureClient-IOS
//
//  Created by Bartol Karuza on 03/07/14.
//  Copyright (c) 2014 Bartol Karuza. All rights reserved.
//

#import "PPViewController.h"
#import "SocketIOPacket.h"
#import "SocketIOJSONSerialization.h"

@interface PPViewController ()

@end

@implementation PPViewController

CLLocationManager *locationManager;

- (void)viewDidLoad
{
    [super viewDidLoad];
    _socketIO = [[SocketIO alloc] initWithDelegate:self];
    [_socketIO connectToHost:@"bartolkaruza-measure-app.nodejitsu.com" onPort:80];
    locationManager = [[CLLocationManager alloc] init];
    locationManager.delegate = self;
    locationManager.desiredAccuracy = kCLLocationAccuracyBest;
    [locationManager startUpdatingLocation];
	// Do any additional setup after loading the view, typically from a nib.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

# pragma mark -
# pragma mark socket.IO-objc delegate methods

- (void) socketIODidConnect:(SocketIO *)socket
{
    NSLog(@"socket.io connected.");
}

- (void) socketIO:(SocketIO *)socket didReceiveEvent:(SocketIOPacket *)packet
{
    NSLog(@"didReceiveEvent()");
}

- (void) socketIO:(SocketIO *)socket onError:(NSError *)error
{
    NSLog(@"onError() %@", error);
}


- (void) socketIODidDisconnect:(SocketIO *)socket disconnectedWithError:(NSError *)error
{
    NSLog(@"socket.io disconnected. did error occur? %@", error);
}

#pragma mark - CLLocationManagerDelegate

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error
{
    NSLog(@"didFailWithError: %@", error);
    UIAlertView *errorAlert = [[UIAlertView alloc]
                               initWithTitle:@"Error" message:@"Failed to Get Your Location" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [errorAlert show];
}

- (void)locationManager:(CLLocationManager *)manager didUpdateToLocation:(CLLocation *)newLocation fromLocation:(CLLocation *)oldLocation
{
    NSLog(@"didUpdateToLocation: %@", newLocation);
    CLLocation *currentLocation = newLocation;
    if (currentLocation != nil) {
        NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
        [formatter setDateFormat:@"MMM, dd, yyyy, hh:mm:ss"];
        
        //Optionally for time zone converstions
        NSString *stringFromDate = [formatter stringFromDate:[NSDate date]];
        
        NSDictionary *data = [NSDictionary dictionaryWithObjectsAndKeys:
                              @"iphone", @"agent",
                              stringFromDate, @"time",
                              [NSString stringWithFormat:@"%.8f", currentLocation.coordinate.latitude], @"latitude",
                              [NSString stringWithFormat:@"%.8f", currentLocation.coordinate.longitude], @"longitude",
                              [NSString stringWithFormat:@"%.8f", currentLocation.altitude], @"altitude",
                               nil];
        [_socketIO sendEvent:@"measurement" withData:data];
    }
}

@end
