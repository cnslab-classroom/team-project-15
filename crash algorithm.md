사용할 센서는 두가지, 가속도 센서와 각속도 센서
x축y축z축의 가속도를 감지하고 총 가속도를 측정함

1. 가속도(Accelation) 센서 --> 가속도를 측정

    Acc_total = root{(Ax)^2 + (Ay)^2 + (Az)^2}
 각속도(Gyroscope) 센서 --> 각속도의 측정  

    Gcc_total = root{(Gx)^2 + (Gy)^2 + (Gz)^2}

2. 차 - 사람 충돌 가속도 계산
   
![cal1](https://github.com/user-attachments/assets/60e96f74-294d-4099-8949-0a65d19e5d32)
![cal2](https://github.com/user-attachments/assets/2c155008-5e98-4cb9-af90-d595a5124652)

반발계수 참고문헌: Van 형 차량의 보행자 충돌 사고 해석 모델(Analytical Model in Pedestrian Accident by Van Type Vehicle)
한국기계가공학회지, v.7 no.4, 2008년, pp.115 - 120 , 안승모 (도로교통안전공단) ,  강대민 (부경대학교 기계공학부)


4. 변화량 감지 알고리즘 

(일단 슈도코드 작성)