

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Player extends Entity {
    private BufferedImage[][] animations;
    private int aniTick, aniIndex, aniSpeed = 25;
    private int playerAction = Constants.PlayerConstants.IDLE;
    private boolean moving = false, attacking = false;
    private boolean left, up, right, down, jump;
    private float playerSpeed = 2.0f;
    private int[][] lvlData;
    private float xDrawOffset = 21 * Game.SCALE;
    private float yDrawOffset = 4 * Game.SCALE;

    // Jumping / Gravity
    private float airSpeed = 0f;
    private float gravity = 0.04f * Game.SCALE;
    private float jumpSpeed = -2.25f * Game.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
    private boolean inAir = false;
    private int counter = 0; 
    public Player(float x, float y, int width, int height) {
        super(x, y, width, height);
        loadAnimations();
        initHitbox(x, y, 20 * Game.SCALE, 27 * Game.SCALE);

    }

    public void update() {
        updatePos();
        updateAnimationTick();
        setAnimation();
    }

    public void render(Graphics g) {
        g.drawImage(animations[playerAction][aniIndex], (int) (hitbox.x - xDrawOffset), (int) (hitbox.y - yDrawOffset), width, height, null);
        //        drawHitbox(g);
    }

    private void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= Constants.PlayerConstants.GetSpriteAmount(playerAction)) {
                aniIndex = 0;
                attacking = false;
            }

        }

    }

    private void setAnimation() {
        int startAni = playerAction;

        if (moving)
            playerAction = Constants.PlayerConstants.RUNNING;
        else
            playerAction = Constants.PlayerConstants.IDLE;

        if (inAir) {
            if (airSpeed < 0)
                playerAction = Constants.PlayerConstants.JUMP;
            else
                playerAction = Constants.PlayerConstants.FALLING;
        }
        
        if(inAir&&attacking){
            attacking = false;
            inAir = false;
            playerAction = Constants.PlayerConstants.ATTACK_JUMP_1;
        }
    
        if (attacking)
            playerAction = Constants.PlayerConstants.ATTACK_1;

        if (startAni != playerAction)
            resetAniTick();
            
        
    }

    private void resetAniTick() {
        aniTick = 0;
        aniIndex = 0;
    }

    private void updatePos() {
        moving = false;

        //if (jump)
        //    jump();
        if (!left && !right && !inAir)
            return;

        float xSpeed = 0;

        if (left)
            xSpeed -= playerSpeed;
        if (right)
            xSpeed += playerSpeed;
        
        if (!inAir)
            if (!HelpMethods.IsEntityOnFloor(hitbox, lvlData)){
                inAir = true;
               
           }
           else{
                ResetCount();
            
            }

        if (inAir) {
            if (HelpMethods.CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
                hitbox.y += airSpeed;
                airSpeed += gravity;
                updateXPos(xSpeed);
            } else {
                hitbox.y = HelpMethods.GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
                if (airSpeed > 0)
                    resetInAir();
                else
                    airSpeed = fallSpeedAfterCollision;
                updateXPos(xSpeed);
            }

        } else
            updateXPos(xSpeed);
        moving = true;
    }

    public void jump() {
        if (inAir)
            return;
        inAir = true;
        airSpeed = jumpSpeed;
        updatePos();
        System.out.println("Ri9zzler Gyat");
    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;

    }

    private void updateXPos(float xSpeed) {
        if (HelpMethods.CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
            hitbox.x += xSpeed;
        } else {
            hitbox.x = HelpMethods.GetEntityXPosNextToWall(hitbox, xSpeed);
        }

    }

    private void loadAnimations() {

        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

        animations = new BufferedImage[9][6];
        for (int j = 0; j < animations.length; j++)
            for (int i = 0; i < animations[j].length; i++)
                animations[j][i] = img.getSubimage(i * 64, j * 40, 64, 40);

    }

    public void loadLvlData(int[][] lvlData) {
        this.lvlData = lvlData;
        if (!HelpMethods.IsEntityOnFloor(hitbox, lvlData))
            inAir = true;

    }

    public void resetDirBooleans() {
        left = false;
        right = false;
        up = false;
        down = false;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }
    
    public void Count(){
        counter++;
    }
    
    public void ResetCount(){
        counter = 0; 
    }
    
    public int GetCount(){
        return counter; 
    }
}
