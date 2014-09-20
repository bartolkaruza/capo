//
//  AppDelegate.swift
//  LightSpaceX
//
//  Created by Volgin, MV - SPL/XL on 20/09/14.
//  Copyright (c) 2014 capo. All rights reserved.
//

import UIKit
import CoreBluetooth
import CoreMotion

let NOTIFY_MTU = 20

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, CBCentralManagerDelegate { //, CBPeripheralManagerDelegate { //, PHBridgeSelectionViewControllerDelegate, PHBridgePushLinkViewControllerDelegate {
    
    // MARK: - CoreBluetooth & CoreMotion
    
    var centralManager: CBCentralManager?
    var peripheralManager: CBPeripheralManager?
    var motionCharacteristic: CBMutableCharacteristic?
    var motionManager: CMMotionManager?
    var referenceFrame: CMAttitude?
    
    func centralManagerDidUpdateState(central: CBCentralManager!) {
        if (central.state == .PoweredOn) {
//            central.scanForPeripheralsWithServices(<#serviceUUIDs: [AnyObject]!#>, optio<#[NSObject : AnyObject]!#>ns: <#[NSObject : AnyObject]!#>)
            central.scanForPeripheralsWithServices(nil, options: nil)
        }
    }
    
    func centralManager(central: CBCentralManager!, didDiscoverPeripheral peripheral: CBPeripheral!, advertisementData: [NSObject : AnyObject]!, RSSI: NSNumber!) {
//        let localName = advertisementData[kCBAdvDataLocalName]
//        println("rssi: \(RSSI) advertisementData: \(advertisementData)")
        println("rssi: \(RSSI) name: \(peripheral.name)")
    }
    
    /** Required protocol method.  A full app should take care of all the possible states,
    *  but we're just waiting for  to know when the CBPeripheralManager is ready
    */
    func peripheralManagerDidUpdateState(peripheral: CBPeripheralManager!) {
        if (peripheral.state == .PoweredOn) {
//            self.motionCharacteristic = CBMutableCharacteristic(
                /*
                [[CBMutableCharacteristic alloc] initWithType:[CBUUID UUIDWithString:MOTION_CHARACTERISTIC_UUID]
                properties:CBCharacteristicPropertyRead | CBCharacteristicPropertyNotify
                value:nil
                permissions:CBAttributePermissionsReadable];
            // Then the service
            CBMutableService *transferService = [[CBMutableService alloc] initWithType:[CBUUID UUIDWithString:MOTION_SERVICE_UUID]
                primary:YES];
            
            // Add the characteristic to the service
            transferService.characteristics = @[self.motionCharacteristic];
            
            // And add it to the peripheral manager
            [self.peripheralManager addService:transferService];
            */
        }
    }
    
    // MARK: - HueSDK
    
    var phHueSDK: PHHueSDK?
    
    // MARK: heartbeat
    
    /**
    Starts the local heartbeat with a 10 second interval
    */
    func enableLocalHeartbeat() {
        /***************************************************
        The heartbeat processing collects data from the bridge
        so now try to see if we have a bridge already connected
        *****************************************************/
        let cache: PHBridgeResourcesCache? = PHBridgeResourcesReader.readBridgeResourcesCache()
        if (cache != nil && cache!.bridgeConfiguration != nil && cache!.bridgeConfiguration.ipaddress != nil) {
//            [self showLoadingViewWithText:NSLocalizedString(@"Connecting...", @"Connecting text")];
//            
            // Enable heartbeat with interval of 10 seconds
            self.phHueSDK?.enableLocalConnection()
        } else {
                
        }
        /*
        #pragma mark - Heartbeat control
        
        - (void)enableLocalHeartbeat {
        
            PHBridgeResourcesCache *cache = [PHBridgeResourcesReader readBridgeResourcesCache];
            if (cache != nil && cache.bridgeConfiguration != nil && cache.bridgeConfiguration.ipaddress != nil) {
                //
            } else {
                // Automaticly start searching for bridges
                [self searchForBridgeLocal];
            }
        }
*/
    }
    
    func disableLocalHeartbeat() {
        
    }
    
    func searchForBridgeLocal() {
        
    }
    
    /*
    // MARK: - SDKWizard
    
    func bridgeSelectedWithIpAddress(ipAddress: String!, andMacAddress macAddress: String!) {
        //
    }
    
    func pushlinkSuccess() {
        //
    }
    
    func pushlinkFailed(error: PHError!) {
        //
    }
    */
    
    // MARK: - notifications
    
    func localConnection() {
        println("localConnection")
    }
    
    func noLocalConnection() {
        println("noLocalConnection")
    }
    
    func notAuthenticated() {
        println("notAuthenticated")
    }

    // MARK: - lifecycle
    
    var window: UIWindow?

    func application(application: UIApplication, didFinishLaunchingWithOptions launchOptions: [NSObject: AnyObject]?) -> Bool {
        // Override point for customization after application launch.
        
        // CM
        self.motionManager = CMMotionManager()
        self.motionManager?.deviceMotionUpdateInterval = 0.1
        
        // CB
        self.centralManager = CBCentralManager(delegate: self, queue: nil)
//        self.peripheralManager = CBPeripheralManager(delegate: self, queue: nil)
        
        // Hue
        self.phHueSDK = PHHueSDK()
        self.phHueSDK?.startUpSDK()
        self.phHueSDK?.enableLogging(true)
        
        /***************************************************
        The SDK will send the following notifications in response to events:
        
        - LOCAL_CONNECTION_NOTIFICATION
        This notification will notify that the bridge heartbeat occurred and the bridge resources cache data has been updated
        
        - NO_LOCAL_CONNECTION_NOTIFICATION
        This notification will notify that there is no connection with the bridge
        
        - NO_LOCAL_AUTHENTICATION_NOTIFICATION
        This notification will notify that there is no authentication against the bridge
        *****************************************************/
        let notificationManager: PHNotificationManager = PHNotificationManager.defaultManager()
        notificationManager.registerObject(self, withSelector: Selector("localConnection"), forNotification: LOCAL_CONNECTION_NOTIFICATION)
        notificationManager.registerObject(self, withSelector: Selector("noLocalConnection"), forNotification: NO_LOCAL_CONNECTION_NOTIFICATION)
        notificationManager.registerObject(self, withSelector: Selector("notAuthenticated"), forNotification: NO_LOCAL_AUTHENTICATION_NOTIFICATION)
        
        
        /*
        self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];

        // Create the main view controller in a navigation controller and make the navigation controller the   of the app
        PHControlLightsViewController *controlLightsViewController = [[PHControlLightsViewController alloc] initWithNibName:@"PHControlLightsViewController" bundle:[NSBundle mainBundle]];
        
        self.navigationController = [[UINavigationController alloc] initWithRootViewController:controlLightsViewController];
        
        self.window.rootViewController = self.navigationController;
        [self.window makeKeyAndVisible];
        
        */
        
        /***************************************************
        The local heartbeat is a regular timer event in the SDK. Once enabled the SDK regular collects the current state of resources managed
        by the bridge into the Bridge Resources Cache
        *****************************************************/
        self.enableLocalHeartbeat()
        return true
    }

    func applicationWillResignActive(application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(application: UIApplication) {
        // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillTerminate(application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
        self.motionManager?.stopDeviceMotionUpdates()
        self.peripheralManager?.stopAdvertising()
        self.phHueSDK?.stopSDK()
    }

}

