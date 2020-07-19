job("task6_j1"){
        scm {
                 github('MananJaiswal/task4devops' , 'master')
             }
        triggers {
                scm("* * * * *")
                
  	}
         


        steps {
        shell('''sudo cp * /html/
	sudo docker build -t mananjaiswal/task6:latest .
	sudo docker push mananjaiswa/task6''')
      }
}


job("task6_j2"){
        triggers {
        upstream {
    upstreamProjects("task6_j1")
    threshold("Fail")
        }
        }
        

        steps {
        shell('''if sudo kubectl get deployment | grep web1
then
echo " updating"
else
sudo kubectl create deployment web1 --image=mananjaiswal/task6:latest
sudo kubectl autoscale deployment web1 --min=2 --max=5 --cpu-percent=80
fi
if sudo kubectl get deployment -o wide | grep latest
then 
sudo kubectl set image deployment web1 task6=mananjaiswal/task6:latest
else
sudo kubectl set image deployment web1 task6=mananjaiswal/task6:latest
fi
if sudo kubectl get service | grep web1
then 
echo "service exist"
else
sudo kubectl expose deployment web1 --port=80 --type=NodePort
fi ''')
      }
}


job("task6_j3"){
        description("Testing pods and sending mail")
        
        triggers {
                upstream {
    upstreamProjects("task6_j2")
    threshold("Fail")
   } 
        }
        


        steps {
        shell('''if sudo kubectl get deployment | grep web1
then
echo " All good"
else
cd /root/
python3 qualityassuredmail.py
fi
''')
      }
}

