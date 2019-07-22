#  部署指南
0. 准备好 mysql 和 java运行环境
1. 创建数据库sited（可字可自定义）
2. 在数据库上加载 /bin/sited.sql，生成相关表和数据
3. 在服务器上下载 /bin/jwitter.jar 到 /data/sss/ (目录可定义)
4. 使用脚本运行服务：java -jar /data/sss/jwitter.jar -host={host} -db=sited -usr={user} -pwd={password}

##### 建议把脚本转为.service文件后通过systemctl操控；或其它更好友的控制脚本
