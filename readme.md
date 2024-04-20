# Image classfincation app
* -> citations:- https://www.youtube.com/watch?v=yV9nrRIC_R0 , https://www.youtube.com/watch?v=So1Bs8CmSa8&t=11s , https://medium.com/@khaingsuthway_72707/unlocking-the-power-of-lightweight-neural-networks-on-mobile-devices-685882ebb51f , https://github.com/Gerrix90/JetpackComposePlayground/tree/main

# Methodology
* In this app i used TensorFlow lite , neural network api and Native code.
* In this I firstly created a neural network and trained it on the dataset available online https://bitbucket.org/ishaanjav/code-and-deploy-custom-tensorflow-lite-model/raw/a4febbfee178324b2083e322cdead7465d6fdf95/fruits.zip
* I divided this dataset into 80:20 i.e. 80% for training and 20% for testing
* This trained model got 95% accuracy on the testing dataset
* After training it I imported this model in my android app
* I used tensor flow lite to create a inference pipeline in android app
* In the inference pipeline during feature extraction step we need to fill byteBuffer for storing information about the image pixel values
* This byteBuffer creation is done in native code 

# Internal Working
* In the home screen there is a select a photo button using which we can select the photo
* after selecting the photo the internal state that store the bitmap of the image get bitmap of the image
* Due to the new value assignment to this internal state app get recompose and show the image and a button for asking the machine about the image on the screen
* This button for the asking machine regarding image when get clicked this in turn called classification function that start classification of the image using the defined inference pipeline by tensorflow API
* After finishing the classification, the result of this classification function get displayed to the screen

# Output
* we can only have three output from this app that are apple , orange and banana

# Implementation details of tensor Flow lite inference pipeline
1) Extracting image features
2) creating 2d matrix called rgbValuesArray 
3) Using this matrix's each and every value created byteBuffer in Native app
4) creating input features using this bytebuffer 
5) Passing this input features to the model and get inferred result
6) inferred result have confidences
7) These confidences define how much confident model is about each target variable that this image is this target variable
8) Using these confidences return the target variable that is having maximum confidence

