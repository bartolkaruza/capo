//
//  PPViewController.h
//  measureClient-IOS
//
//  Created by Bartol Karuza on 03/07/14.
//  Copyright (c) 2014 Bartol Karuza. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SocketIO.h"
#import <CoreLocation/CoreLocation.h>

@interface PPViewController : UIViewController <SocketIODelegate, CLLocationManagerDelegate>
@property (nonatomic,strong) SocketIO* socketIO;

@end
