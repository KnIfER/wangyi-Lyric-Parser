package db;


public class dbFields{
    String  KEY_LYRIC     ;
    String  SONG_PATH     ;
    String  LRC_PATH      ;
    String  TIME_LINE     ;
    String  TIME_LINE2    ;
    String  LRC_MAIN      ;
    String  LRC_SUB       ;
    String  TITLE         ;
    String  PORJECT_CREATE;

    public void dbFields(
            String _KEY_LYRIC ,
            String _SONG_PATH ,
            String _LRC_PATH  ,
            String _TIME_LINE ,
            String _TIME_LINE2,
            String _LRC_MAIN  ,
            String _LRC_SUB,
            String _TITLE         ,
            String _PORJECT_CREATE
    ) {
        KEY_LYRIC =_KEY_LYRIC ;
        SONG_PATH =_SONG_PATH ;
        LRC_PATH  =_LRC_PATH  ;
        TIME_LINE =_TIME_LINE ;
        TIME_LINE2=_TIME_LINE2;
        LRC_MAIN  =_LRC_MAIN  ;
        LRC_SUB   =_LRC_SUB   ;
        TITLE         =_TITLE         ;
        PORJECT_CREATE=_PORJECT_CREATE;
    }


}