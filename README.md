# 🤖 Machine Learning
Bangkit Capstone 2023 Repository for Emo Tunes App

 - The ML models used for final deliverables is "EmoTunes_Model_without_Vectorization.ipynb"
 - Datasets used for training: cleaned_train.txt, val.txt
 - Datasets used for testing: test.txt

steps:
1. import the source datasets, and check for redundancies: same sentence with different emotion label
2. Use NLTK to remove stopwords and do lemmatization
3. do tokenization and pad sequences for the datasets
4. create model layer using embedding, Conv1d, GlobalMaxpooling1D, ANN layer, and output layer consist of 6 output label
5. do model compile and model fit

6A. if you want to deploy via mobile device, convert the model into TFLite and get the word tokenized as in tokenizerV2.JSON

6B. if you want to deploy via cloud platform, convert the model into SavedModel format via zip and download it
