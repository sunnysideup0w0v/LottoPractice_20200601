package com.example.lottopractice_20200601;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lottopractice_20200601.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;
    int[] winLottoNumArr = new int[6];
    int bonusNum = 0;
    List<TextView> winNumTxts = new ArrayList<>();
    long useMoney = 0L;

    List<TextView> myNumTxts = new ArrayList<>();
    long winMoney = 0L;

    int firstRankCount = 0;
    int secondRankCount = 0;
    int thirdRankCount = 0;
    int fourthRankCount = 0;
    int fifthRankCount = 0;
    int unrankedCount = 0;

    boolean isAutoBuyRunning = false;

    Handler mHandler = new Handler();
    Runnable buyLottoRunnable = new Runnable() {
        @Override
        public void run() {
            if(useMoney<10000000){
                makeLottoWinNumbers();
                checkWinRank();
            } else {
                Toast.makeText(mContext, "로또 구매를 종료합니다.", Toast.LENGTH_SHORT).show();
            }
            mHandler.post(buyLottoRunnable);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setupEvents();
        setValues();
    }

    @Override
    public void setupEvents() {
//        한 장 구매시
        binding.buyOneLottoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeLottoWinNumbers();
                checkWinRank();
            }
        });

//        자동으로 구매시
        binding.buyAutoLottoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                지금 구매를 안 돌리고 있다면
                if(!isAutoBuyRunning){
//                    구매 시작 코드를 할 일로 등록시키자 => mHandler가 실행
                    mHandler.post(buyLottoRunnable);
//                    구매가 돌아가고 있다고 명시
                    isAutoBuyRunning = true;
//                    버튼의 문구도 중단하기로 변경
                    binding.buyAutoLottoBtn.setText(getResources().getString(R.string.pause_auto_buying));
                } else {
//                    구매가 돌아가고 있다면
//                    예정된 다음 구매 행동을 할일에서 제거함.
//                    더 이상 할 일이 없으니 자동으로 정지됨.
                    mHandler.removeCallbacks(buyLottoRunnable);
//                    지금 구매중이 아니라고 명시.
                    isAutoBuyRunning = false;
//                    다시 누르면 재개한다고 알려줌.
                    binding.buyAutoLottoBtn.setText(getResources().getString(R.string.resume_auto_buying));
                }
            }
        });
    }

    @Override
    public void setValues() {
//        당첨번호 텍스트뷰들을 => ArrayList에 담아둠
//        당첨번호를 적어줄 때 편리하게 짜려고
        winNumTxts.add(binding.winNumTxt01);
        winNumTxts.add(binding.winNumTxt02);
        winNumTxts.add(binding.winNumTxt03);
        winNumTxts.add(binding.winNumTxt04);
        winNumTxts.add(binding.winNumTxt05);
        winNumTxts.add(binding.winNumTxt06);

//        내 입력 번호도 동일하게 적어줌줌
       myNumTxts.add(binding.myNumTxt01);
        myNumTxts.add(binding.myNumTxt02);
        myNumTxts.add(binding.myNumTxt03);
        myNumTxts.add(binding.myNumTxt04);
        myNumTxts.add(binding.myNumTxt05);
        myNumTxts.add(binding.myNumTxt06);

    }

    void makeLottoWinNumbers(){
        for(int i=0; i<winLottoNumArr.length;i++){
            winLottoNumArr[i] = 0;
        }
        bonusNum = 0;

        for(int i=0;i<winLottoNumArr.length;i++){
            while(true){
                int randomNum = (int)(Math.random()*45+1);
                boolean isDuplicatedOk = true;
                for(int num : winLottoNumArr){
                    if(num==randomNum){
                        isDuplicatedOk = false;
                        break;
                    }
                }
                if (isDuplicatedOk) {
                    winLottoNumArr[i] = randomNum;
                    break;
                }
            }
        }
        Arrays.sort(winLottoNumArr);
        while(true){
            int randomNum = (int)(Math.random()*45+1);
            boolean isDuplicatedOk = true;
            for(int num: winLottoNumArr){
                if(num==randomNum){
                    isDuplicatedOk = false;
                    break;
                }
            }
            if(isDuplicatedOk){
                bonusNum = randomNum;
                break;
            }
        }

        for(int i=0;i<winNumTxts.size();i++){
            int winNum = winLottoNumArr[i];

            winNumTxts.get(i).setText(winNum+"");
        }
        binding.bonusNumTxt.setText(bonusNum+"");
    }

//   등수 확인 코드
    void checkWinRank(){
//        사용금액 증가
        useMoney += 1000;
//        증가된 금액을 화면에 반영(중복 코드)
        binding.useMoneyTxt.setText(String.format("%,d원",useMoney));
//        맞춘 갯수 저장 변수
        int correctCount = 0;
//        내 입력 번호가 적힌 텍스트뷰들 (setValues 참고) 꺼내봄
        for(TextView myNumTxt: myNumTxts){
//            적혀있는 숫자(String)를 int로 변경
            int myNum = Integer.parseInt(myNumTxt.getText().toString());
//            내 숫자, 당첨번호의 숫자 비교
            for(int winNum: winLottoNumArr){
//                마이넘txt > 스트링 > integer 클래스 이용해서 > int로 파싱
                if(myNum == winNum){
//                    같은 숫자를 찾았다면 count 1 증가
                    correctCount++;
                }
            }
        }
//        진짜로 등수를 체크하고, 당첨금액을 넘겨주는 if문
        if(correctCount==6){
            winMoney += 1300000000;
            firstRankCount++;
        } else if (correctCount==5){
            boolean isBonusNumCorrect = false;
            for(TextView myNumTxt: myNumTxts){
                int myNum = Integer.parseInt(myNumTxt.getText().toString());
                if(myNum == bonusNum){
                    isBonusNumCorrect = true;
                    break;
                }
            }
            if(isBonusNumCorrect){
                winMoney+=54000000;
                secondRankCount++;
            } else {
                winMoney+=1450000;
                thirdRankCount++;
            }
        } else if(correctCount == 4){
            winMoney += 50000;
            fourthRankCount++;
        } else if(correctCount == 3){
            useMoney -= 5000;
            fifthRankCount++;
        } else {
            unrankedCount++;
        }

        binding.winMoneyTxt.setText(String.format("%,d원",winMoney));
        binding.useMoneyTxt.setText(String.format("%,d원",useMoney));

        binding.firstRankTxt.setText(String.format("%d회",firstRankCount));
        binding.secondRankTxt.setText(String.format("%d회",secondRankCount));
        binding.thirdRankTxt.setText(String.format("%d회",thirdRankCount));
        binding.fourthRankTxt.setText(String.format("%d회",fourthRankCount));
        binding.fifthRankTxt.setText(String.format("%d회",fifthRankCount));
        binding.unrankedTxt.setText(String.format("%d회",unrankedCount));

    }
}
