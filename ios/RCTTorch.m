//
//  RCTTorch.m
//  Cubicphuse
//
//  Created by Ludo van den Boom on 06/04/2017.
//  Copyright © 2017 Cubicphuse. All rights reserved.
//

#import <AVFoundation/AVFoundation.h>
#import "RCTTorch.h"

@implementation RCTTorch

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(switchState:(nonnull NSNumber*)newState)
{
    if ([AVCaptureDevice class]) {
        AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
        if ([device hasTorch]){
            [device lockForConfiguration:nil];
            
            if ([newState boolValue]) {
                [device setTorchMode:AVCaptureTorchModeOn];
            } else {
                [device setTorchMode:AVCaptureTorchModeOff];
            }
            
            [device unlockForConfiguration];
        }
    }
}

RCT_EXPORT_METHOD(getStatus:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    if ([AVCaptureDevice class]) {
        AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
        if ([device hasTorch]){
            BOOL isOn = device.torchMode == AVCaptureTorchModeOn;
            resolve([NSNumber numberWithBool:isOn]);
        } else {
            NSError *error = [[NSError alloc] initWithDomain:@"torch" code:0 userInfo:nil];
            reject(@"no_torch_available", @"This device has no torch", error);
        }
    } else {
        NSError *error = [[NSError alloc] initWithDomain:@"torch" code:0 userInfo:nil];
        reject(@"no_torch_available", @"This device has no torch", error);
    }
}

@end
