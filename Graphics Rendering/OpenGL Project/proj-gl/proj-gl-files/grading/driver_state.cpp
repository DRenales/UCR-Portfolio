#include "driver_state.h"
#include <cstring>

using namespace std;

driver_state::driver_state()
{
}

driver_state::~driver_state()
{
    delete [] image_color;
    delete [] image_depth;
}

// This function should allocate and initialize the arrays that store color and
// depth.  This is not done during the constructor since the width and height
// are not known when this class is constructed.
void initialize_render(driver_state& state, int width, int height)
{
    state.image_width=width;
    state.image_height=height;
    state.image_color= new pixel[width*height];
    state.image_depth = new float[width*height];
    //std::cout<<"TODO: allocate and initialize state.image_color and state.image_depth."<<std::endl;

    for(int pos = 0; pos <(width * height); pos++)
    {
	     state.image_color[pos] = make_pixel(0,0,0);
	     state.image_depth[pos] = 1.0;
    }
}

// This function will be called to render the data that has been stored in this class.
// Valid values of type are:
//   render_type::triangle - Each group of three vertices corresponds to a triangle.
//   render_type::indexed -  Each group of three indices in index_data corresponds
//                           to a triangle.  These numbers are indices into vertex_data.
//   render_type::fan -      The vertices are to be interpreted as a triangle fan.
//   render_type::strip -    The vertices are to be interpreted as a triangle strip.
void render(driver_state& state, render_type type)
{
    //std::cout<<"TODO: implement rendering."<<std::endl;
    switch(type)
    {
	     case render_type::triangle:
       {
	        //cout << "Triangle Render" << endl;
          data_geometry** tri = new data_geometry*[3];

	        for(int vertices = 0; vertices < state.num_vertices; vertices += 3)
          {
		          for(int point = 0; point < 3; point++)
              {
		              tri[point] = new data_geometry;
		              data_vertex vertex;
		              vertex.data = new float[MAX_FLOATS_PER_VERTEX];
		              tri[point]->data = new float[MAX_FLOATS_PER_VERTEX];

		              for(int pos = 0; pos < state.floats_per_vertex; pos++)
                  {
			                 vertex.data[pos] = state.vertex_data[pos + state.floats_per_vertex * (vertices + point)];
			                 tri[point]->data[pos] = vertex.data[pos];
                  }

                  state.vertex_shader((const data_vertex)vertex, *tri[point], state.uniform_data);
		          }

		      //rasterize_triangle(state, (const data_geometry**)tri);
          clip_triangle(state, (const data_geometry**)tri, 0);
	       }
	    } break;

      case render_type::indexed:
      {
        const data_geometry *geo[3];
	      data_geometry tri[3];
	      data_vertex vertex[3];

	      for(int i = 0; i < (state.num_triangles * 3); i += 3)
        {
	         for(int j = 0; j < 3; j++)
           {
		           vertex[j].data = &state.vertex_data[state.index_data[i + j] * state.floats_per_vertex];
		           tri[j].data = vertex[j].data;
		           state.vertex_shader(vertex[j], tri[j], state.uniform_data);
		           geo[j] = &tri[j];
	         }

          clip_triangle(state,geo,0);
	      }

      } break;

      case render_type::fan:
      {
        const data_geometry* geo[3];
	      data_geometry tri[3];
	      data_vertex vertex[3];
	      int points;

	      for(int i = 0; i < state.num_vertices; i++)
        {
	         for(int j = 0; j < 3; j++)
           {
		           points = i+j;
		           if(j == 0 ) points = 0;

		           vertex[j].data = &state.vertex_data[points * state.floats_per_vertex];
		           tri[j].data = vertex[j].data;
		           state.vertex_shader(vertex[j], tri[j], state.uniform_data);
		           geo[j] = &tri[j];
	         }
	         clip_triangle(state, geo, 0);
        }
      } break;

      case render_type::strip:
      {
        const data_geometry* geo[3];
	      data_geometry tri[3];
	      data_vertex vertex[3];

	      for(int i = 0; i < (state.num_vertices -2); i++)
        {
	         for(int j = 0; j < 3; j++)
           {
		           vertex[j].data = &state.vertex_data[(i + j) * state.floats_per_vertex];
		           tri[j].data = vertex[j].data;
		           state.vertex_shader(vertex[j], tri[j], state.uniform_data);
	             geo[j] = &tri[j];
	         }

           clip_triangle(state, geo, 0);
	      }
      } break;

      default:
      {} break;
    }
}


// This function clips a triangle (defined by the three vertices in the "in" array).
// It will be called recursively, once for each clipping face (face=0, 1, ..., 5) to
// clip against each of the clipping faces in turn.  When face=6, clip_triangle should
// simply pass the call on to rasterize_triangle.
void clip_triangle(driver_state& state, const data_geometry* in[3],int face)
{
  if( face == 1)
  {
	rasterize_triangle(state, in);
	return;
  }
//    std::cout<<"CLIPPING"<<endl;

  vec4 a = in[0]->gl_Position;
  vec4 b = in[1]->gl_Position;
  vec4 c = in[2]->gl_Position;

  const data_geometry* inn[3] = {in[0], in[1], in[2]};
  data_geometry data1[3];
  data_geometry data2[3];

  float a1, b1, b2;
  vec4 p1, p2;

  if( a[2] < -a[3] && b[2] < -b[3] && c[2] < -c[2]) return;
  else
  {
	   if( a[2] < -a[3] && b[2] >= -b[3] && c[2] >= -c[3])
     {
	      b1 = ( -b[2] - b[3] ) / ( a[2] + a[3] - b[2] - b[3] );
	      b2 = ( -a[2] - a[3] ) / ( c[2] + c[3] - a[2] - a[3] );

	      p1 = b1 * a + (1 - b1) * b;
	      p2 = b2 * c + (1 - b2) * a;

	      data1[0].data = new float[state.floats_per_vertex];
	      data1[1] = *in[1];
	      data1[2] = *in[2];

	      for(int i = 0; i < state.floats_per_vertex; i++)
        {
		        switch(state.interp_rules[i])
            {
		            case interp_type::noperspective:
                {
			               a1 = b2 * in[2]->gl_Position[3] / (b2 * in[2]->gl_Position[3] + (1 - b2) * in[0]->gl_Position[3]);
			               data1[0].data[i] = a1 * in[2]->data[i] + (1 - a1) * in[0]->data[i];
		            } break;

                case interp_type::flat:
                {
			               data1[0].data[i] = in[0]->data[i];
		            } break;

                case interp_type::smooth:
                {
			               data1[0].data[i] = b2 * in[2]->data[i] + (1 -b2) * in[0]->data[i];
		            } break;

		            default:
		            {} break;
		        }
	     }
	     data1[0].gl_Position = p2;
	     inn[0] = &data1[0];
	     inn[1] = &data1[1];
	     inn[2] = &data1[2];


	     clip_triangle(state, inn, face + 1);
	    //happens 6ihs times
	     data2[0].data = new float[state.floats_per_vertex];
	     data2[2] = *in[2];

	     for(int i = 0; i < state.floats_per_vertex; ++i)
       {
	        switch(state.interp_rules[i])
          {
		          case interp_type::noperspective:
              {
		              a1 = b1 * in[0]->gl_Position[3] / (b1 * in[0]->gl_Position[3] + (1 - b1) * in[1]->gl_Position[3]);
		              data2[0].data[i] = a1 * in[0]->data[i] + ( 1 - a1 ) *in [1]->data[i];
		          } break;

              case interp_type::flat:
              {
			            data2[0].data[i] = in[0]->data[i];
		          } break;

              case interp_type::smooth:
              {
			            data2[0].data[i] = b1 * in[0]->data[i] + (1 - b1) * in[1]->data[i];
		          } break;

              default:
		          {} break;
	    	}
	    }

	    data2[0].gl_Position = p1;
	    inn[0] = &data2[0];
	    inn[1] = &data1[1];
	    inn[2] = &data1[0];
	  }
    clip_triangle(state, inn, face + 1);
  }
}

// Rasterize the triangle defined by the three vertices in the "in" array.  This
// function is responsible for rasterization, interpolation of data to
// fragments, calling the fragment shader, and z-buffering.
void rasterize_triangle(driver_state& state, const data_geometry* in[3])
{
  int index; // index
  float alpha, beta, gamma; //aplha beta and gamma
  float a_x, a_y, b_x, b_y, c_x, c_y; //points for a b and c
  float abc, ab_p, ac_p, bc_p; //areas


  a_x = (state.image_width/2.0) * (in[0]->gl_Position[0]/in[0]->gl_Position[3]) + (state.image_width/2.0) - 0.5;
  a_y = (state.image_height/2.0) * (in[0]->gl_Position[1]/in[0]->gl_Position[3]) + (state.image_height/2.0) - 0.5;

  b_x = (state.image_width/2.0) * (in[1]->gl_Position[0]/in[1]->gl_Position[3]) + (state.image_width/2.0) - 0.5;
  b_y = (state.image_height/2.0) * (in[1]->gl_Position[1]/in[1]->gl_Position[3]) + (state.image_height/2.0) - 0.5;

  c_x = (state.image_width/2.0) * (in[2]->gl_Position[0]/in[2]->gl_Position[3]) + (state.image_width/2.0) - 0.5;
  c_y = (state.image_height/2.0) * (in[2]->gl_Position[1]/in[2]->gl_Position[3]) + (state.image_height/2.0) - 0.5;

  float min_x = min(a_x, min(b_x, c_x));
  float min_y = min(a_y, min(b_y, c_y));
  float max_x = max(a_x, max(b_x, c_x));
  float max_y = max(a_y, max(b_y, c_y));

  abc = 0.5 * ((b_x * c_y - c_x * b_y) + (c_x * a_y - a_x * c_y) + (a_x * b_y - b_x * a_y));

  if(min_x < 0) min_x = 0;
  if(min_y < 0)	min_y = 0;
  if(max_x > state.image_width)	max_x = state.image_width;
  if(max_y > state.image_height) max_y = state.image_height;

  for(int y = min_y; y < max_y; y++)
  {
	   for(int x = min_x; x < max_x; x++)
     {
	      index = x + y * state.image_width;

        bc_p = 0.5 * ((b_x * c_y - c_x * b_y) - (x * c_y - y * c_x) + (x * b_y - y * b_x));
        ac_p = 0.5 * ((x * c_y - y * c_x) - (a_x * c_y - c_x * a_y) + (y * a_x - x * a_y));
        ab_p = 0.5 * ((y * b_x - x * b_y) - (y * a_x - x * a_y) + (a_x * b_y - b_x * a_y));

	      alpha = bc_p/abc;
	      beta  = ac_p/abc;
	      gamma = ab_p/abc;

	      //if(alpha >=0 && beta>=0 && gamma>=0) state.image_color[index] = make_pixel(255,255,255);
        if(alpha >= 0 && beta >= 0 && gamma >= 0)
        {
          data_fragment fragment;
        	fragment.data = new float[MAX_FLOATS_PER_VERTEX];
        	data_output output;

        	float delta = alpha * in[0]->gl_Position[2]/in[0]->gl_Position[3] + beta * in[1]->gl_Position[2]/in[1]->gl_Position[3] + gamma * in[2]->gl_Position[2]/in[2]->gl_Position[3];

        	if(state.image_depth[index] > delta)
          {
        	   for(int h = 0; h < state.floats_per_vertex; h++)
             {
               switch(state.interp_rules[h])
               {
            	    case interp_type::noperspective:
                  {
            			  fragment.data[h] = alpha * in[0]->data[h] + beta * in[1]->data[h] + gamma * in[2]->data[h];
            			} break;

            			case interp_type::flat:
                  {
                    fragment.data[h] = in[0]->data[h];
                  } break;

            			case interp_type::smooth:
                  {
                    float k = (alpha / in[0]->gl_Position[3]) + (beta / in[1]->gl_Position[3]) + (gamma / in[2]->gl_Position[3]);
                    float _alpha = alpha / k / in[0]->gl_Position[3];
                    float _beta = beta / k / in[1]->gl_Position[3];
                    float _gamma = gamma / k / in[2]->gl_Position[3];
                    fragment.data[h] = _alpha * in[0]->data[h] + _beta * in[1]->data[h] + _gamma * in[2]->data[h];
                  } break;

            			default:
            			{} break;
        			 }
        		}

        		state.fragment_shader(fragment, output, state.uniform_data);
        	  output.output_color = output.output_color * 255;
        	  state.image_color[index] = make_pixel(output.output_color[0], output.output_color[1], output.output_color[2]);
        		state.image_depth[index] = delta;
          }
       }
    }
  }
}
