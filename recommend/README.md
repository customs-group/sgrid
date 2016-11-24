# sgrid_recommend_only

copy of sgrid's recommend module



## 数据清理步骤

1. 用LibreOffice打开所需清理的文件

2. 日期格式标准化

   选中日期相关列 - 右键 - Format Cells - 选中 Numbers 标签下的 Date Category - 在 Format 中选中所需格式，建议为 yyyy-MM-dd HH:mm:ss

3. 将 "\\" 替换为 "/"

   由于反斜杠("\")在 java 中有特殊含义，为避免造成错误的解析，需要将文本中的反斜杠全部替换为正斜杠("/")

   选中可能含有反斜杠的列（通常为内容是大段文字的列，如缺陷描述、处理意见、解决方案等） - Edit - Find and Replace - 选中 Other options 中的 Regular expressions - Search For 中填写"\\\\"(不包括括号) - Replace With 中填写"/"(不包括括号) - Replace All

4. 将 "\n" 替换为 ""（去除一段文字中不应该出现的换行符）

   由于文件读入时是按行读入，若在内容中含有换行符，则会将本应在一行中读入的内容分割成多行，产生数据错误。

   选中可能含有"\n"的列（通常为内容是大段文字的列，如缺陷描述、处理意见、解决方案等，若不确定也可选中所有内容）- Edit - Find and Replace - 选中 Other options 中的 Regular expressions - Search For 中填写"\n"(不包括引号) - Replace With 中什么都不填 - Replace All

5. 将**非公式列**中的 ","（半角逗号）替换为 "，"（全角逗号）

   由于csv文件以","(半角逗号)作为分隔符，如果内容中也含有半角逗号，则会使文件读入时将一列的内容分割为多列，造成数据错误。此外，在中文文本中使用全角逗号也更合理。

   选中可能含有"\n"的列（通常为内容是大段文字的列，如缺陷描述、处理意见、解决方案等）- Edit - Find and Replace - 选中 Other options 中的 Regular expressions - Search For 中填写"\n"(不包括引号) - Replace With 中什么都不填 - Replace All

6. 将文件另存为csv格式



## Quick Start

通过initFromXXX方法读入缺陷库，现已实现的有initFromCSVFile，initFromDB（initFromDB需要根据所用数据库再作调整）。

读入的过程中程序会自动对缺陷库进行训练，在训练完成后便可调用recommend方法，传入想要被推荐的缺陷描述，以及所需展示的推荐条目数量（可选，默认为1），便可获得推荐的解决方案列表。



## 简介

本程序的训练内容为缺陷库中所有的缺陷描述，对其进行分词，并提取若干关键词用以表示一条缺陷，再将关键词训练为向量，形成一个包含了所有缺陷描述的向量空间。在使用时，将新输入的文本同样进行分词、提取关键词和训练为向量，将新形成的向量放入训练完的向量空间中寻找与之最接近的几个向量（代表了缺陷库中的缺陷描述），推荐它们所对应的解决方法等缺陷数据。

基本原理见：

http://www.ruanyifeng.com/blog/2013/03/tf-idf.html

http://www.ruanyifeng.com/blog/2013/03/cosine_similarity.html

