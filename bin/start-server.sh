if [ -z "$SECKILL_HOME" ]; then
  echo "SECKILL_HOME not found"
  echo "Please export SECKILL_HOME to your environment variable"
  exit
fi

cd $SECKILL_HOME
Lib_dir=`ls | grep lib`
if [ -z "$Lib_dir" ]; then
  echo "Invalid SECKILL_HOME"
  exit
fi

Server=`ps -ef | grep java | grep com.hackhu.seckill.SeckillApplication | grep -v grep | awk '{print $2}'`
if [[ $Server -gt 0 ]]; then
  echo "[Seckill Server] is already started"
  exit
fi

cd $SECKILL_HOME
TODAY=`date "+%Y-%m-%d"`
LOG_PATH=$SECKILL_HOME/logs/sys/Seckill.$TODAY.log
nohup java -Dfile.encoding=UTF-8 -cp $JAVA_HOME/lib/*:lib/* com.hackhu.seckill.SeckillApplication > $LOG_PATH  2>&1 &

echo "=========================================="
echo "Starting..., press \`CRTL + C\` to exit log"
echo "=========================================="

sleep 3s
tail -f $LOG_PATH