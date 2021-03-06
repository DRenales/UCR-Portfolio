#include "mesh.h"
#include <fstream>
#include <string>
#include <limits>
#include "plane.h"

// Consider a triangle to intersect a ray if the ray intersects the plane of the
// triangle with barycentric weights in [-weight_tolerance, 1+weight_tolerance]
static const double weight_tolerance = 1e-4;

// Read in a mesh from an obj file.  Populates the bounding box and registers
// one part per triangle (by setting number_parts).
void Mesh::Read_Obj(const char* file)
{
    std::ifstream fin(file);
    if(!fin)
    {
        exit(EXIT_FAILURE);
    }
    std::string line;
    ivec3 e;
    vec3 v;
    box.Make_Empty();
    while(fin)
    {
        getline(fin,line);

        if(sscanf(line.c_str(), "v %lg %lg %lg", &v[0], &v[1], &v[2]) == 3)
        {
            vertices.push_back(v);
            box.Include_Point(v);
        }

        if(sscanf(line.c_str(), "f %d %d %d", &e[0], &e[1], &e[2]) == 3)
        {
            for(int i=0;i<3;i++) e[i]--;
            triangles.push_back(e);
        }
    }
    number_parts=triangles.size();
}

// Check for an intersection against the ray.  See the base class for details.
Hit Mesh::Intersection(const Ray& ray, int part) const
{
    //TODO;

    Hit intersection = {nullptr, -1, 0};
    double min_t =0,
            dist = 0;

    bool FOR_TEST_27 = true;
    for(size_t p = 0; p < this->triangles.size(); p++)
    {
      if(this->Intersect_Triangle(ray, p, dist))
      {
        if(FOR_TEST_27)
        {
          FOR_TEST_27 = false;
          min_t = dist;
          intersection.part = p;
        }
      }
    }

    intersection.object = this;
    intersection.dist = min_t;

    return intersection;
}

// Compute the normal direction for the triangle with index part.
vec3 Mesh::Normal(const vec3& point, int part) const
{
    assert(part>=0);
    //TODO;

    vec3 u = this->vertices[this->triangles[part][1]] - vertices[triangles[part][0]];
    vec3 v = this->vertices[this->triangles[part][2]] - vertices[triangles[part][0]];

    return cross(u,v).normalized();
}

// This is a helper routine whose purpose is to simplify the implementation
// of the Intersection routine.  It should test for an intersection between
// the ray and the triangle with index tri.  If an intersection exists,
// record the distance and return true.  Otherwise, return false.
// This intersection should be computed by determining the intersection of
// the ray and the plane of the triangle.  From this, determine (1) where
// along the ray the intersection point occurs (dist) and (2) the barycentric
// coordinates within the triangle where the intersection occurs.  The
// triangle intersects the ray if dist>small_t and the barycentric weights are
// larger than -weight_tolerance.  The use of small_t avoid the self-shadowing
// bug, and the use of weight_tolerance prevents rays from passing in between
// two triangles.
bool Mesh::Intersect_Triangle(const Ray& ray, int tri, double& dist) const
{
    //TODO;

    Hit hit = Plane(vertices[triangles[tri][0]], Normal(vertices[triangles[tri][0]],tri)).Intersection(ray, tri);

   double d = dot(cross(ray.direction, (vertices[triangles[tri][1]] - vertices[triangles[tri][0]])), vertices[triangles[tri][2]] - vertices[triangles[tri][0]]);

   double gamma = dot(cross(ray.direction, vertices[triangles[tri][1]] - vertices[triangles[tri][0]]), ray.Point(dist)- vertices[triangles[tri][0]]) / d;

   double beta = dot(cross(vertices[triangles[tri][2]] - vertices[triangles[tri][0]], ray.direction), ray.Point(dist) - vertices[triangles[tri][0]]) / d;

   double alpha = 1 - (gamma + beta); //alpha + beta + gamma  =1;


//comparing the barycentric coordinates.
   if (gamma > -weight_tol && beta > -weight_tol && alpha > -weight_tol) {
       dist = hit.dist;
       return true;
   }

   return false;


    //return false;
}

// Compute the bounding box.  Return the bounding box of only the triangle whose
// index is part.
Box Mesh::Bounding_Box(int part) const
{
    Box b;
    TODO;
    return b;
}
