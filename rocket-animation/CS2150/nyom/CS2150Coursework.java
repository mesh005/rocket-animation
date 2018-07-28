/* CS2150Coursework.java

 * TODO: put your university username and full name here: Mesheriff N-yo (nyom)
 *
 * Scene Graph:
 *  Scene origin
 *  |
 *  +-- [S(25,1,20) T(0,-1,-10)] Ground plane
 *  |
 *  +-- [S(25,1,10) Rx(90) T(0,4,-20)] Back plane
 *  |
 *  +-- [Rx(-90) S(2,2,2) T(5,-1,-13)] Rocketbody
 *  |    |
 *  |    +-- [Rx(-90) T(5,2,-13)] RocketHead
 *  |    |
 *  |    +-- [S(0.5,1,1) Ry(90) Rx(270)T (4.5,-1,-13)] Left rocket wing
 *  |    |
 *  |    +-- [S(0.5,1,1) Ry(90) T(5.5,-1,-13)] Right rocket wing
 *  |
 *  |
 *  +-- [S(0.3,0.7,0.3) T(-2.5,-1,-5)] Pole 
 *  |    |
 *  |   +--[S(2,0.5,1) T(2.3,2.2,0)] flag
 *  |
 *  +--[T(-6,3,-15)] star1
 *  |
 *  +-- [T(-7.5,3,-13.5)] Star2
 *  |
 *  +-- [T(5,4,-13)] Star3
 *  |
 *  +-- [T(4,3,-14.5)] Star4
 *
 *
 *
 *  TODO: Provide a scene graph for your submission
 */
package nyom;

import org.lwjgl.opengl.GL11;



import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.opengl.Texture;
import GraphicsLab.*;


/**
 * TODO: Briefly describe your submission here
 *Space themed project with animations to simulate a rocket landing on the moon and shooting stars in the background.
 * <p>Controls:
 * <ul>
 * <li>Press the escape key to exit the application.
 * <li>Hold the x, y and z keys to view the scene along the x, y and z axis, respectively
 * <li>While viewing the scene along the x, y or z axis, use the up and down cursor keys
 *      to increase or decrease the viewpoint's distance from the scene origin
 *      <li> Press T to raise the rocket
 *      <li> Press L to lower the rocket
 * </ul>
 * TODO: Add any additional controls for your sample to the list above
 *
 */
public class CS2150Coursework extends GraphicsLab
{
	/* display list for rocket wings */
	private final int rightWingList = 1;
	private final int leftWingList = 2;
	private final int planeList = 3;
	private final int poleList = 4;
	private final int flagList= 5;
	
	/** the rockets current Y offset from the scene origin */
    private float currentRocketY = -1.0f;
    /** the sun/moon's highest possible Y offset */
    private final float highestRocketY = 20.0f;
    /** the sun/moon's lowest possible Y offset */
    private final float lowestRocketY  = currentRocketY;
    /** is the rocket rising? (true = the rocket is rising) */
    private boolean risingRocket = false;
	 
    private float time = 0.0f;
    private float opp = 0.0f;
    
    
	private Texture groundTexture;
	private Texture skyTexture;
	private Texture flag;
	//TODO: Feel free to change the window title and default animation scale here
    public static void main(String args[])
    {   new CS2150Coursework().run(WINDOWED,"CS2150 Coursework Submission",0.01f);
    }

    protected void initScene() throws Exception
    {//TODO: Initialise your resources here - might well call other methods you write.
    	 // global ambient light level
    	
    	//load textures
        groundTexture = loadTexture("nyom/textures/moonGround.bmp");
        skyTexture = loadTexture("nyom/textures/spaceBackground.bmp");
    	flag = loadTexture("nyom/textures/flag.bmp");
    	
    	float globalAmbient[]   = {0.2f,  0.2f,  0.2f, 1.0f};
        // set the global ambient lighting
        GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT,FloatBuffer.wrap(globalAmbient));
        // the first light for the scene is white...
        float diffuse0[]  = { 0.6f,  0.6f, 0.6f, 1.0f};
        // ...with a dim ambient contribution...
        float ambient0[]  = { 0.1f,  0.1f, 0.1f, 1.0f};
        // ...and is positioned above the viewpoint
        float position0[] = { 0.0f, 10.0f, 0.0f, 1.0f}; 

        // supply OpenGL with the properties for the first light
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, FloatBuffer.wrap(ambient0));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, FloatBuffer.wrap(diffuse0));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, FloatBuffer.wrap(diffuse0));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, FloatBuffer.wrap(position0));
        // enable the first light
        GL11.glEnable(GL11.GL_LIGHT0);
        
      //enable lighting calculations
        GL11.glEnable(GL11.GL_LIGHTING);
        // ensure that all normals are re-normalised after transformations automatically
        GL11.glEnable(GL11.GL_NORMALIZE);
   
        //Display Lists
      GL11.glNewList(rightWingList,GL11.GL_COMPILE);
      {   drawRocketWings();
        }
      GL11.glEndList();
      
      GL11.glNewList(leftWingList,GL11.GL_COMPILE);
      {   drawRocketWings();
        }
      GL11.glEndList();
        
     GL11.glNewList(planeList,GL11.GL_COMPILE);
      {   drawUnitPlane();
    }
      GL11.glEndList();
      
      GL11.glNewList(poleList,GL11.GL_COMPILE);
      {   drawPole();
        }
      GL11.glEndList();
    
    GL11.glNewList(flagList,GL11.GL_COMPILE);
    {     drawFlag();
      }
    GL11.glEndList();
  }
    
  
    protected void checkSceneInput()
    {//TODO: Check for keyboard and mouse input here
    	if(Keyboard.isKeyDown(Keyboard.KEY_T))
        {   risingRocket = true;
        }
        else if(Keyboard.isKeyDown(Keyboard.KEY_L))
        {   risingRocket = false;
        }
    }
    protected void updateScene()
    {
        //TODO: Update your scene variables here - remember to use the current animation scale value
        //        (obtained via a call to getAnimationScale()) in your modifications so that your animations
        //        can be made faster or slower depending on the machine you are working on
    	 
    	// if the rocket is rising, and it isn't at its highest,
        // then increment the rocketsY offset
        if(risingRocket && currentRocketY < highestRocketY)
        {   currentRocketY += 1.0f * getAnimationScale();
        }
        // else if the rocket is falling, and it isn't at its lowest,
        // then decrement the rockets Y offset
        else if(!risingRocket && currentRocketY > lowestRocketY)
        {   currentRocketY -= 1.0f * getAnimationScale();
        }
        time += + 0.01f * getAnimationScale();
        opp -= 0.01f * getAnimationScale();
        
    }
    protected void renderScene()
    {//TODO: Render your scene here - remember that a scene graph will help you write this method! 
     //      It will probably call a number of other methods you will write.
    	
    	 // draw the ground plane
        GL11.glPushMatrix();
        {
            GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
            GL11.glDisable(GL11.GL_LIGHTING);
           
            // change the geometry colour to white so that the texture
            // is bright and details can be seen clearly
            Colour.WHITE.submit();
            // enable texturing and bind an appropriate texture
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D,groundTexture.getTextureID());
            
            // position, scale and draw the ground plane using its display list
            GL11.glTranslatef(0.0f,-1.0f,-10.0f);
            GL11.glScaled(25.0f, 1.0f, 20.0f);
            GL11.glCallList(planeList);

            // disable textures and reset any local lighting changes
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glPopAttrib();
        }
        GL11.glPopMatrix();
        
        // draw the back plane
        GL11.glPushMatrix();
        {
            GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
            GL11.glDisable(GL11.GL_LIGHTING);
            
            // change the geometry colour to white so that the texture
            // is bright and details can be seen clearly
            Colour.WHITE.submit();
        	
            // enable texturing and bind an appropriate texture
            GL11.glEnable(GL11.GL_TEXTURE_2D);
           GL11.glBindTexture(GL11.GL_TEXTURE_2D, skyTexture.getTextureID());
          
           // position, scale and draw the back plane using its display list
           GL11.glTranslatef(0.0f,4.0f,-20.0f);
           GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
           GL11.glScaled(25.0f, 1.0f, 10.0f);
           GL11.glCallList(planeList);
           
           // disable textures and reset any local lighting changes
           GL11.glDisable(GL11.GL_TEXTURE_2D);
          GL11.glPopAttrib();
        }
        GL11.glPopMatrix();
        
        
    	//draw rocketBody
    	GL11.glPushMatrix();
    	{          
            // shininess of the front faces of rocket base (specular exponent)
            float rocketBodyFrontShininess  = 60.0f;
            // specular reflection of the front faces of rocket base
            float rocketBodyFrontSpecular[] = {0.1f, 0.1f, 0.0f, 1.0f};
            // diffuse reflection of the front faces of rocket base
            float rocketBodyDiffuse[]  = {0.5f, 0.5f, 0.5f, 1.0f};
            
            // material properties for the rocket base
            GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, rocketBodyFrontShininess);
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(rocketBodyFrontSpecular));
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(rocketBodyDiffuse));
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT, FloatBuffer.wrap(rocketBodyDiffuse));
    		
            //position and draw rocket body using cylinder quadric object
            GL11.glTranslatef(5.0f, currentRocketY, -13.0f);
            GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
            new Cylinder().draw(1,1,3,5,5);
            	}
    	GL11.glPopMatrix();
    	
    	//draw rocket head
    	GL11.glPushMatrix();
    	{
   
             // shininess of the front face of rocket head (specular exponent)
    		float rocketHeadFrontShininess  = 2.0f;
            // specular reflection of front face of rocket head
           float rocketHeadFrontSpecular[] = {0.1f, 0.1f, 0.0f, 1.0f};
            // diffuse reflection of the front face of rocket head
            float rocketHeadDiffuse[]  = {0.8f, 0.0f, 0.0f, 1.0f};
            
       // Set the material properties for rocket head
           GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, rocketHeadFrontShininess );
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(rocketHeadFrontSpecular));
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(rocketHeadDiffuse));
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT, FloatBuffer.wrap(rocketHeadDiffuse));
            
            //position and draw rocket head using cylinder quadric object
           GL11.glTranslatef(5.0f, (currentRocketY+3.0f), -13.0f);
            GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
             new Cylinder().draw(1,0,1,5,5);
    	}
    	GL11.glPopMatrix();
    	
    	//draw right rocket wing
    	GL11.glPushMatrix();
    	{
    	    GL11.glTranslatef(5.5f, currentRocketY, -13.0f);
    		GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
    		GL11.glScalef(0.5f, 1.0f, 1.0f);
    		float rightWingFrontShininess  = 2.0f;
            // specular reflection of the wing
            float rightWingFrontSpecular[] = {0.1f, 0.1f, 0.0f, 1.0f};
            // diffuse reflection of the wing
            float rightWingDiffuse[]  = {0.8f, 0.0f, 0.0f, 1.0f};
            
         // Set the material properties for the rocket wing
            GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, rightWingFrontShininess );
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(rightWingFrontSpecular));
           GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(rightWingDiffuse));
           GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT, FloatBuffer.wrap(rightWingDiffuse));
          
           GL11.glCallList(rightWingList);
    	 }
    	GL11.glPopMatrix();
    	
    	//draw left rocket wing
    	GL11.glPushMatrix();
    	{
    	    GL11.glTranslatef(4.5f, currentRocketY, -13.0f);
    		GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
    		GL11.glRotatef(270.0f, 1.0f, 0.0f, 0.0f);
    		GL11.glScalef(0.5f, 1.0f, 1.0f);
    		
    		float leftWingFrontShininess  = 2.0f;
            // specular reflection of the wing
            float leftWingFrontSpecular[] = {0.1f, 0.1f, 0.0f, 1.0f};
           //  diffuse reflection of the wing
            float leftWingDiffuse[]  = {0.8f, 0.0f, 0.0f, 1.0f};
            
         // Set the material properties for the rocketwing
            GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, leftWingFrontShininess );
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(leftWingFrontSpecular));
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(leftWingDiffuse));
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT, FloatBuffer.wrap(leftWingDiffuse));
            
            GL11.glCallList(leftWingList);
    		}
    	GL11.glPopMatrix();
        
    
  //draw pole and flag
	GL11.glPushMatrix();
	{
		//draw pole
	 GL11.glTranslatef(-2.5f, -1.0f, -5.0f);
	 GL11.glScalef(0.3f, 0.7f, 0.3f);
		float poleShininess  = 60.0f;
         //specular reflection of the pole
        float poleSpecular[] = {0.1f, 0.0f, 0.0f, 1.0f};
      //   diffuse reflection of the pole
       float poleDiffuse[]  = {0.6f, 0.2f, 0.2f, 1.0f};
       
      //Set the material properties for the pole
        GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, poleShininess );
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(poleSpecular));
       GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(poleDiffuse));
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT, FloatBuffer.wrap(poleDiffuse));
        
         GL11.glCallList(poleList);
        
       //draw flag
         GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
        GL11.glDisable(GL11.GL_LIGHTING);
       
        // change the geometry colour to white so that the texture
        // is bright and details can be seen clearly
        Colour.WHITE.submit();
        
        // enable texturing and bind an appropriate texture
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D,flag.getTextureID());
       
        //position and scale flag
        GL11.glTranslatef(2.3f, 2.2f, 0.0f);
		GL11.glScalef(2.0f, 0.5f, 1.0f);
	    GL11.glCallList(flagList);
        
	    // disable textures and reset any local lighting changes
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glPopAttrib();
		
	}
	GL11.glPopMatrix();
    
	//draw shooting star 1
	GL11.glPushMatrix();
      {
     //specular exponent of star
          float starFrontShininess  = 2.0f;
          // specular reflection of the star
          float starFrontSpecular[] = {0.1f, 0.1f, 0.0f, 1.0f};
          // diffuse reflection of the star
          float starFrontDiffuse[]  = {0.52f, 0.52f, 0.52f, 1.0f};
          
          // set the material properties for the star
          GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, starFrontShininess);
          GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(starFrontSpecular));
          GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(starFrontDiffuse));
          // moves with time
          GL11.glTranslatef((-6.0f+time),3.0f,(-15.0f-time));
          // draw star
         new Sphere().draw(0.2f,20,20); 
         }
      GL11.glPopMatrix();
   
    //draw shooting star 2
  	GL11.glPushMatrix();
        {
            // moves with time
            GL11.glTranslatef((-7.5f+time),(3.0f+time),(-13.5f-time));
            // draw star
           new Sphere().draw(0.2f,20,20); 
           }
        GL11.glPopMatrix();
        
      //draw shooting star 3
      	GL11.glPushMatrix();
            {
                // moves with time
                GL11.glTranslatef((5.0f+opp),4.0f,(-13.0f-time));
                // draw star
               new Sphere().draw(0.2f,20,20); 
               }
            GL11.glPopMatrix();
            
          //draw shooting star 4
          	GL11.glPushMatrix();
                {
                    // moves with time
                    GL11.glTranslatef((4.0f+opp),(3.0f+opp),(-14.5f+opp));
                    // draw star
                   new Sphere().draw(0.2f,20,20); 
                   }
                GL11.glPopMatrix();
      }    
    
    protected void setSceneCamera()
    {
        // call the default behaviour defined in GraphicsLab. This will set a default perspective projection
        // and default camera settings ready for some custom camera positioning below...  
        super.setSceneCamera();

        //TODO: If it is appropriate for your scene, modify the camera's position and orientation here
        //        using a call to GL11.gluLookAt(...)
   }

    protected void cleanupScene()
    {//TODO: Clean up your resources here
      
    }
    
    private void drawUnitPlane()
    {
        Vertex v1 = new Vertex(-0.5f, 0.0f,-0.5f); // left,  back
        Vertex v2 = new Vertex( 0.5f, 0.0f,-0.5f); // right, back
        Vertex v3 = new Vertex( 0.5f, 0.0f, 0.5f); // right, front
        Vertex v4 = new Vertex(-0.5f, 0.0f, 0.5f); // left,  front
        
        // draw the plane geometry. order the vertices so that the plane faces up
        GL11.glBegin(GL11.GL_POLYGON);
        {
            new Normal(v4.toVector(),v3.toVector(),v2.toVector(),v1.toVector()).submit();
            
            GL11.glTexCoord2f(0.0f,0.0f);
            v4.submit();
            
            GL11.glTexCoord2f(1.0f,0.0f);
            v3.submit();
            
            GL11.glTexCoord2f(1.0f,1.0f);
            v2.submit();
            
            GL11.glTexCoord2f(0.0f,1.0f);
            v1.submit();
        }
        GL11.glEnd();
        
        // if the user is viewing an axis, then also draw this plane
        // using lines so that axis aligned planes can still be seen
        if(isViewingAxis())
        {
            // also disable textures when drawing as lines
            // so that the lines can be seen more clearly
            GL11.glPushAttrib(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBegin(GL11.GL_LINE_LOOP);
            {
                v4.submit();
                v3.submit();
                v2.submit();
                v1.submit();
            }
            GL11.glEnd();
            GL11.glPopAttrib();
        }
    }
    private void drawRocketWings()
    {
    	 
    	{
    	    Vertex v1 = new Vertex(-1,1,0);
    	    Vertex v2 = new Vertex(1,1,0);
    	    Vertex v3 = new Vertex(-1,0,0);
    	    Vertex v4 = new Vertex(1,0,0);
    	    Vertex v5 = new Vertex(-1,0,1);
    	    Vertex v6 = new Vertex(1,0,1);
    	 
    	    
    	  //front face
    	    GL11.glBegin(GL11.GL_POLYGON);
    	    {
    	    	v1.submit();
    	    	v5.submit();
    	    	v6.submit();
    	    	v2.submit();
    	        	   
    	    }
    		GL11.glEnd();	
    		
    		//Left face
    		 GL11.glBegin(GL11.GL_POLYGON);
     	    {
     	    	v1.submit();
     	    	v3.submit();
     	    	v5.submit();

     	    }
     		GL11.glEnd();	
     		
     		//Right face
     		 GL11.glBegin(GL11.GL_POLYGON);
     	    {
     	    	v2.submit();
     	    	v4.submit();
     	    	v6.submit();
     	    }
     		GL11.glEnd();	
     		
     		//Back face
     		 GL11.glBegin(GL11.GL_POLYGON);
     	    {
     	    	v2.submit();
     	    	v4.submit();
     	    	v3.submit();
     	    	v1.submit();
     	    }
     		GL11.glEnd();	
     		
     		//Bottom face
     		GL11.glBegin(GL11.GL_POLYGON);
     	    {
     	    	v4.submit();
     	    	v6.submit();
     	    	v5.submit();
     	    	v3.submit();
     	    }
     		GL11.glEnd();	
     		
     	}
    }
    
    private void drawPole()
    {
    	
    	Vertex v1 = new Vertex(-0.2f, 3.0f, -0.2f);
		Vertex v2 = new Vertex(-0.2f, 3.0f, 0.2f);
		Vertex v3 = new Vertex(-0.2f, 0.0f, -0.2f);
		Vertex v4 = new Vertex(-0.2f, 0.0f, 0.2f);
		Vertex v5 = new Vertex(0.2f, 3.0f, -0.2f);
		Vertex v6 = new Vertex(0.2f, 3.0f, 0.2f);
		Vertex v7 = new Vertex(0.2f, 0.0f, -0.2f);
		Vertex v8 = new Vertex(0.2f, 0.0f, 0.2f);	 

		
		//top face
		
		GL11.glBegin(GL11.GL_POLYGON);
		{		
	     v1.submit();
	     v2.submit();
	     v6.submit();
	     v5.submit();
		}
		GL11.glEnd();
		
		//bottom face
		
		GL11.glBegin(GL11.GL_POLYGON);
		{			
			v7.submit();
		     v8.submit();
		     v4.submit();
		     v3.submit();
			}
		GL11.glEnd();
		
		//front face
		
		GL11.glBegin(GL11.GL_POLYGON);
				{					
					v2.submit();
				     v4.submit();
				     v8.submit();
				     v6.submit();
				}
				GL11.glEnd();
				
		//back face
				
		GL11.glBegin(GL11.GL_POLYGON);
		{			
			v5.submit();
		     v7.submit();
		     v3.submit();
		     v1.submit();			
		}
		GL11.glEnd();
				
		//left face
		
				GL11.glBegin(GL11.GL_POLYGON);
				{					
					v1.submit();
				     v3.submit();
				     v4.submit();
				     v2.submit();		
				}
				GL11.glEnd();
				
				//right face
			
				GL11.glBegin(GL11.GL_POLYGON);
				{					
					v6.submit();
				     v8.submit();
				     v7.submit();
				     v5.submit();		
				}
				GL11.glEnd();
    }
    
    private void drawFlag()
    {
    	Vertex v1 = new Vertex(-1.0f, -1.0f, 0.0f);
		Vertex v2 = new Vertex(1.0f, -1.0f, 0.0f);
		Vertex v3 = new Vertex(1.0f, 1.0f, 0.0f);
		Vertex v4 = new Vertex(-1.0f, 1.0f, 0.0f);
   //front face
		GL11.glBegin(GL11.GL_POLYGON);
		{
			 
			GL11.glTexCoord2f(0.0f,0.0f);
			v1.submit();
			
			GL11.glTexCoord2f(1.0f,0.0f);
			v2.submit();
		     
			 GL11.glTexCoord2f(1.0f,1.0f);
			v3.submit();
			 
			GL11.glTexCoord2f(0.0f,1.0f);
		     v4.submit();
			}
		GL11.glEnd();
		
		//back face
		GL11.glBegin(GL11.GL_POLYGON);
		{
			v2.submit();
		     v1.submit();
		     v4.submit();
		     v3.submit();
			}
		GL11.glEnd();
    }
    
    }
