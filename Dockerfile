# 文件路径: 12345-es-project/Dockerfile

# 使用官方的 Elasticsearch 8.6.2 镜像作为基础
FROM elasticsearch:8.6.2

# 将我们本地的、已验证的插件zip包，复制到镜像内部的/tmp目录下
# 请确保 elasticsearch-analysis-ik-8.6.2.zip 文件与此 Dockerfile 文件在同一目录
COPY elasticsearch-analysis-ik-8.6.2.zip /tmp/ik.zip

# 从镜像内部的本地文件进行离线安装，并使用 -b 参数自动确认权限
# 这一步没有任何网络访问，100%可靠
RUN bin/elasticsearch-plugin install -b file:///tmp/ik.zip
