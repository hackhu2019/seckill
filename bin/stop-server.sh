Server=`ps -ef | grep java | grep com.hackhu.seckill.SeckillApplication | grep -v grep | awk '{print $2}'`
if [[ $Server -gt 0 ]]; then
  kill -9 $Server
else
  echo "[Seckill Server] System did not run."
fi