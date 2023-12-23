# 🤖 Machine Learning
Bangkit Capstone 2023 Repository for Emo Tunes App

 - The ML models used for final deliverables is "EmoTunes_Model_without_Vectorization.ipynb"
 - for the final deliverables, the model used is saved into SavedModel format instead of TFLite format
 - Datasets used for training: cleaned_train.txt, val.txt
 - Datasets used for testing: test.txt

steps:
1. import the source datasets, and check for redundancies: same sentence with different emotion label
2. Use NLTK to remove stopwords and do lemmatization
3. do tokenization and pad sequences for the datasets
4. create model layer using embedding, Conv1d, GlobalMaxpooling1D, ANN layer, and output layer consist of 6 output label
5. do model compile and model fit
6. save the model into SavedModel format, zip, and domwload it to deploy into Cloud Platform

note: the model can be deployed into mobile device by convert it into TFLite format and get the list of word tokenized as in tokenizerV2.JSON
