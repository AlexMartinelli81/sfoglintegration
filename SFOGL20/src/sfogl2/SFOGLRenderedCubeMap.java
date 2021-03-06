package sfogl2;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLES2;

public class SFOGLRenderedCubeMap {

	private int sizeX,sizeY;
    private int useDepth;

	private int vp[]=new int[4];
	
    private SFOGLCubeMap texture=new SFOGLCubeMap();
    private SFOGLRenderBuffer renderBuffer=new SFOGLRenderBuffer();
    private SFOGLFrameBuffer frameBuffer=new SFOGLFrameBuffer();
    
    public SFOGLRenderedCubeMap(){
    	
    }
    
    public SFOGLCubeMap getTexture() {
		return texture;
	}
	
    public SFOGLRenderedCubeMap(int sizeX,int sizeY,boolean useDepth){
	    this.sizeX=sizeX;
	    this.sizeY=sizeY;
	    this.useDepth=useDepth?1:0;
	}
	
    public int getTextureObject(){
	    return this.texture.getTextureObject();
	}
	
    public void applyTexture(GL2ES2 gl,int textureLevel){
	    texture.apply(gl,textureLevel);
	}
    
    public void destroy(GL2ES2 gl){
	    texture.delete(gl);
        boolean useDepth=((this.useDepth&1)==1);
	    if(useDepth)
	    	renderBuffer.destroy(gl);
	    frameBuffer.destroy(gl);
	}
    
    public void prepare(GL2ES2 gl){
    	gl.glGetIntegerv(GL.GL_VIEWPORT, vp, 0);
	    if(texture.getTextureObject()==SFOGLTexture.NULL_OBJECT){
	        texture.setup(gl,sizeX, sizeY);
	        boolean useDepth=((this.useDepth&1)==1);
	        if(useDepth)
	            renderBuffer.setup(gl,sizeX, sizeY);
	        frameBuffer.prepare(gl);
	        frameBuffer.apply(gl);
	        if(useDepth)
	            frameBuffer.attachRenderBuffer(gl,GLES2.GL_DEPTH_ATTACHMENT, renderBuffer);
	        SFOGLFrameBuffer.checkFrameBuffer(gl);
	        frameBuffer.unapply(gl);
	    }
	}
	
    public void apply(GL2ES2 gl,int index){
	    frameBuffer.apply(gl);
        frameBuffer.attachCubeMap(gl,GLES2.GL_COLOR_ATTACHMENT0, texture, index);
	    gl.glClearColor(1, 1, 1, 1);
	    boolean useDepth=((this.useDepth&1)==1);
	    gl.glClear(GLES2.GL_COLOR_BUFFER_BIT | ((useDepth)?GLES2.GL_DEPTH_BUFFER_BIT:0));
	    if(!useDepth){
	      gl.glDisable(GLES2.GL_DEPTH_TEST);
	    }
	    gl.glViewport(0, 0, sizeX, sizeY);
	}
	
    public void unapply(GL2ES2 gl){
	    frameBuffer.unapply(gl);
	    gl.glEnable(GLES2.GL_DEPTH_TEST);
		gl.glViewport(0, 0, vp[2],vp[3]);
	}
}
